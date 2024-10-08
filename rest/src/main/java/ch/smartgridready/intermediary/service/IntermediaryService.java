package ch.smartgridready.intermediary.service;


import ch.smartgridready.intermediary.entity.ConfigurationValue;
import ch.smartgridready.intermediary.entity.Device;
import ch.smartgridready.intermediary.exception.DeviceNotFoundException;
import ch.smartgridready.intermediary.exception.DeviceOperationFailedException;
import ch.smartgridready.intermediary.repository.DeviceRepository;
import com.smartgridready.ns.v0.DeviceFrame;
import com.smartgridready.ns.v0.InterfaceList;
import com.smartgridready.ns.v0.MessagingInterface;
import com.smartgridready.ns.v0.RestApiInterface;
import com.smartgridready.communicator.common.api.GenDeviceApi;
import com.smartgridready.communicator.common.api.values.Value;
import com.smartgridready.communicator.common.helper.DeviceDescriptionLoader;
import com.smartgridready.communicator.messaging.client.HiveMqtt5MessagingClientFactory;
import com.smartgridready.communicator.messaging.impl.SGrMessagingDevice;
import com.smartgridready.communicator.modbus.impl.SGrModbusDevice;
import com.smartgridready.communicator.rest.exception.RestApiAuthenticationException;
import com.smartgridready.communicator.rest.http.client.ApacheHttpRequestFactory;
import com.smartgridready.communicator.rest.impl.SGrRestApiDevice;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.http.GenHttpRequestFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.driver.api.modbus.GenDriverAPI4Modbus;

import de.re.easymodbus.adapter.GenDriverAPI4ModbusRTU;
import de.re.easymodbus.adapter.GenDriverAPI4ModbusTCP;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntermediaryService {

    private static final Logger LOG = LoggerFactory.getLogger(IntermediaryService.class);

    private static final String CONFIG_COM_PORT = "comPort";

    private static final String CONFIG_BAUD_RATE = "baudRate";

    private static final String CONFIG_IP_ADDR = "ipAddress";

    private static final String CONFIG_PORT = "port";

    private final DeviceRepository deviceRepository;

    private final Map<String, GenDeviceApi> deviceRegistry = new HashMap<>();

    private final Map<String, String> errorDeviceRegistry = new HashMap<>();

    private final GenHttpRequestFactory httpRequestFactory;

    private final GenMessagingClientFactory messagingClientFactory;

    public IntermediaryService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.httpRequestFactory = new ApacheHttpRequestFactory();
        this.messagingClientFactory = new HiveMqtt5MessagingClientFactory();
    }

    public void loadDevices() {

        var devices = deviceRepository.findAll();
        devices.forEach(this::loadDevice);
    }

    public void loadDevice(Device device) {

        Properties properties = new Properties();
        device.getConfigurationValues().forEach(configurationValue -> properties.put(configurationValue.getName(), configurationValue.getVal()));

        DeviceFrame deviceFrame = new DeviceDescriptionLoader().load(device.getEiXml().getXml(), properties);

        // Handle modbus RTU device
        Optional.ofNullable(deviceFrame.getInterfaceList())
                .map(InterfaceList::getModbusInterface)
                .map(modbusInterface -> modbusInterface.getModbusInterfaceDescription().getModbusRtu())
                .ifPresent(modbusRtu -> addModbusRtuDevice(device.getName(), device.getConfigurationValues(), deviceFrame ));

        // Handle modbus TCP device
        Optional.ofNullable(deviceFrame.getInterfaceList())
                .map(InterfaceList::getModbusInterface)
                .map(modbusInterface -> modbusInterface.getModbusInterfaceDescription().getModbusTcp())
                .ifPresent(modbusTcp -> addModbusTcpDevice(device.getName(), device.getConfigurationValues(), deviceFrame ));

        // Handle REST-API device
        Optional.ofNullable(deviceFrame.getInterfaceList())
                .map(InterfaceList::getRestApiInterface)
                .map(RestApiInterface::getRestApiInterfaceDescription)
                .ifPresent(restApiInterfaceDescription -> addRestApiDevice(device.getName(), deviceFrame) );

        // Handle Messaging device
        Optional.ofNullable(deviceFrame.getInterfaceList())
                .map(InterfaceList::getMessagingInterface)
                .map(MessagingInterface::getMessagingInterfaceDescription)
                .ifPresent(messagingApiInterfaceDescription -> addMessagingDevice(device.getName(), deviceFrame));

    }

    public Value getVal(String deviceName, String functionalProfileName, String dataPointName) throws DeviceOperationFailedException, GenDriverException {

        var device = findDeviceInRegistries(deviceName);

        if (device instanceof SGrMessagingDevice messagingDevice) {
            messagingDevice.subscribe(functionalProfileName, dataPointName, this::mqttCallbackFunction);
        }

        try {
            return device.getVal(functionalProfileName, dataPointName);
        } catch (Exception e) {
            throw new DeviceOperationFailedException(deviceName, e);
        }
    }

    public void setVal(String deviceName, String functionalProfileName, String dataPointName, Value value) throws DeviceOperationFailedException {

        var device = findDeviceInRegistries(deviceName);
        try {
            device.setVal(functionalProfileName, dataPointName, value);
        } catch (Exception e) {
            throw new DeviceOperationFailedException(deviceName, e);
        }
    }

    public String getDeviceStatus(String deviceName) {

        final String UNKNOWN = "UNKNOWN";
        var status = errorDeviceRegistry.entrySet().stream()
                .filter(entry -> entry.getKey().equals(deviceName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(UNKNOWN);

        if (UNKNOWN.equals(status)) {
            status = deviceRegistry.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(deviceName))
                    .map(entry -> "OK")
                    .findFirst()
                    .orElse(UNKNOWN);
        }
        return status;
    }

    private void addModbusRtuDevice(String deviceName, List<ConfigurationValue> configurationValues, DeviceFrame deviceFrame) {

        var comPortProperty = configurationValues.stream()
                .filter(configurationValue -> CONFIG_COM_PORT.equals(configurationValue.getName()))
                .findFirst();

        if (comPortProperty.isEmpty()) {
            var errorMsg = String.format("Cannot instantiate modbus RTU device named '%s'. Property 'comPort' is missing.", deviceName);
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }

        var baudRateProperty = configurationValues.stream()
                .filter(configurationValue -> CONFIG_BAUD_RATE.equals(configurationValue.getName()))
                .findFirst();

        try {
            GenDriverAPI4Modbus deviceDriver;
            if (baudRateProperty.isPresent()) {
                deviceDriver = new GenDriverAPI4ModbusRTU(comPortProperty.get().getVal(), Integer.parseInt(baudRateProperty.get().getVal()));
            } else {
                deviceDriver = new GenDriverAPI4ModbusRTU(comPortProperty.get().getVal());
            }
            deviceDriver.connect();

            deviceRegistry.put(deviceName, new SGrModbusDevice(deviceFrame, deviceDriver));
            LOG.info("Successfully added modbus RTU device named '{}'.", deviceName);
        } catch (GenDriverException e) {
            var errorMsg = String.format("Cannot instantiate modbus RTU device named '%s' on COM port '%s', threw: %s",
                    deviceName, comPortProperty.get().getVal() ,e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }
    }

    private void addModbusTcpDevice(String deviceName, List<ConfigurationValue> configurationValues, DeviceFrame deviceFrame) {

        var ipAddressProperty = configurationValues.stream()
                .filter(cofigurationValue -> CONFIG_IP_ADDR.equals(cofigurationValue.getName()))
                .findFirst();

        if (ipAddressProperty.isEmpty()) {
            var errorMsg = String.format("Cannot instantiate modbus TCP device named'%s'. Property 'ipAddress' is missing.", deviceName);
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }

        var portProperty = configurationValues.stream()
                .filter(configurationValue -> CONFIG_PORT.equals(configurationValue.getName()))
                .findFirst();
        if (portProperty.isEmpty()) {
            var errorMsg = String.format("Cannot instantiate modbus TCP device named '%s' Property 'port' is missing.", deviceName);
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }
        
        try {
            var deviceDriver = new GenDriverAPI4ModbusTCP(ipAddressProperty.get().getVal(), Integer.parseInt(portProperty.get().getVal()));
            deviceDriver.connect();

            deviceRegistry.put(deviceName, new SGrModbusDevice(deviceFrame, deviceDriver));
            LOG.info("Successfully added modbus TCP device named '{}'.", deviceName);
        } catch (Exception e) {
            var errorMsg = String.format("Cannot instantiate modbus TCP device named '%s', threw: %s", deviceName, e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }
    }

    private void addRestApiDevice(String deviceName, DeviceFrame deviceFrame) {
        try {
            var device = new SGrRestApiDevice(deviceFrame, httpRequestFactory);
            deviceRegistry.put(deviceName, device);
            LOG.info("Successfully added REST-API device named '{}'.", deviceName);
        } catch (RestApiAuthenticationException e) {
            var errorMsg = String.format("Cannot instantiate REST API device named'%s'. Device init threw: %s", deviceName, e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
        }
    }

    @SuppressWarnings("java:S2095")
    private void addMessagingDevice(String deviceName, DeviceFrame deviceFrame) {
        try {
            var device = new SGrMessagingDevice(deviceFrame, messagingClientFactory);
            device.connect();
            deviceRegistry.put(deviceName, device);
        } catch (GenDriverException e) {
            var errorMsg = String.format("Cannot instantiate Messaging device API named '%s'. Device init threw: %s", deviceName, e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
        }
    }

    private GenDeviceApi findDeviceInRegistries(String deviceName) {

        var deviceOpt = Optional.ofNullable(deviceRegistry.get(deviceName));
        if (deviceOpt.isEmpty()) {
            var deviceError = Optional.ofNullable(errorDeviceRegistry.get(deviceName));
            if (deviceError.isPresent()) {
                throw new DeviceOperationFailedException(deviceError.get());
            } else {
                throw new DeviceNotFoundException(deviceName);
            }
        }
        return deviceOpt.get();
    }

    private void mqttCallbackFunction(Either<Throwable, Value> message) {
        LOG.info("Received value: {}", message);
    }
}
