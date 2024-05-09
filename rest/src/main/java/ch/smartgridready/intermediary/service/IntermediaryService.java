package ch.smartgridready.intermediary.service;


import ch.smartgridready.intermediary.entity.ConfigurationValue;
import ch.smartgridready.intermediary.entity.Device;
import ch.smartgridready.intermediary.exception.DeviceNotFoundException;
import ch.smartgridready.intermediary.exception.DeviceOperationFailedException;
import ch.smartgridready.intermediary.repository.DeviceRepository;
import com.smartgridready.ns.v0.DeviceFrame;
import com.smartgridready.ns.v0.InterfaceList;
import com.smartgridready.ns.v0.RestApiInterface;
import communicator.common.api.values.Value;
import communicator.common.helper.DeviceDescriptionLoader;
import communicator.common.impl.SGrDeviceBase;
import communicator.common.runtime.GenDriverException;
import communicator.modbus.impl.SGrModbusDevice;
import communicator.rest.exception.RestApiAuthenticationException;
import communicator.rest.http.client.ApacheRestServiceClientFactory;
import communicator.rest.impl.SGrRestApiDevice;
import de.re.easymodbus.adapter.GenDriverAPI4ModbusRTU;
import de.re.easymodbus.adapter.GenDriverAPI4ModbusTCP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntermediaryService {

    private static final Logger LOG = LoggerFactory.getLogger(IntermediaryService.class);

    private static final String CONFIG_COM_PORT = "comPort";

    private static final String CONFIG_IP_ADDR = "ipAddress";

    private static final String CONFIG_PORT = "port";

    private final DeviceRepository deviceRepository;

    private final Map<String, SGrDeviceBase<?, ?, ?>> deviceRegistry = new HashMap<>();

    private final Map<String, String> errorDeviceRegistry = new HashMap<>();

    public IntermediaryService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void loadDevices() {

        var devices = deviceRepository.findAll();
        devices.forEach(this::loadDevice);
    }

    public void loadDevice(Device device) {

        Properties properties = new Properties();
        device.getConfigurationValues().forEach(configurationValue -> properties.put(configurationValue.getName(), configurationValue.getVal()));

        DeviceFrame deviceFrame = (DeviceFrame) new DeviceDescriptionLoader<>().load(device.getEiXml().getXml(), properties);

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

    }

    public Value getVal(String deviceName, String functionalProfileName, String dataPointName) throws DeviceOperationFailedException {

        var device =  findDeviceInRegistries(deviceName);
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

        var deviceDriver = new GenDriverAPI4ModbusRTU();
        try {
            deviceDriver.initTrspService(comPortProperty.get().getVal());
        } catch (GenDriverException e) {
            var errorMsg = String.format("Cannot instantiate modbus RTU device named '%s' on COM port '%s' initTrspService threw: %s",
                    deviceName, comPortProperty.get().getVal() ,e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }

        deviceRegistry.put(deviceName, new SGrModbusDevice(deviceFrame, deviceDriver));
        LOG.info("Successfully added modbus RTU device named '{}'.", deviceName);
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

        var deviceDriver = new GenDriverAPI4ModbusTCP();
        try {
            deviceDriver.initDevice(ipAddressProperty.get().getVal(), Integer.parseInt(portProperty.get().getVal()));
        } catch (Exception e) {
            var errorMsg = String.format("Cannot instantiate modbus TCP device named '%s'. Device init threw: %s", deviceName, e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
            return;
        }

        deviceRegistry.put(deviceName, new SGrModbusDevice(deviceFrame, deviceDriver));
        LOG.info("Successfully added modbus TCP device named '{}'.", deviceName);
    }

    private void addRestApiDevice(String deviceName, DeviceFrame deviceFrame) {
        try {
            var device = new SGrRestApiDevice(deviceFrame, new ApacheRestServiceClientFactory());
            deviceRegistry.put(deviceName, device);
            LOG.info("Successfully added REST-API device named '{}'.", deviceName);
        } catch (RestApiAuthenticationException e) {
            var errorMsg = String.format("Cannot instantiate REST APO device named'%s'. Device init threw: %s", deviceName, e.getMessage());
            LOG.error(errorMsg);
            errorDeviceRegistry.put(deviceName, errorMsg);
        }
    }

    private SGrDeviceBase<?, ?, ?> findDeviceInRegistries(String deviceName) {

        var deviceOpt =  Optional.ofNullable(deviceRegistry.get(deviceName));
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
}
