package ch.smartgridready.intermediary.service;


import ch.smartgridready.intermediary.entity.Device;
import ch.smartgridready.intermediary.exception.DeviceNotFoundException;
import ch.smartgridready.intermediary.exception.DeviceOperationFailedException;
import ch.smartgridready.intermediary.repository.DeviceRepository;
import com.smartgridready.communicator.common.api.SGrDeviceBuilder;
import com.smartgridready.communicator.modbus.api.ModbusGatewayFactory;
import com.smartgridready.communicator.modbus.api.ModbusGatewayRegistry;
import com.smartgridready.communicator.modbus.impl.SGrModbusGatewayRegistry;
import com.smartgridready.communicator.common.api.GenDeviceApi;
import com.smartgridready.communicator.common.api.values.Value;
import com.smartgridready.communicator.messaging.impl.SGrMessagingDevice;
import com.smartgridready.communicator.rest.exception.RestApiAuthenticationException;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.http.GenHttpRequestFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;

import io.vavr.control.Either;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntermediaryService {

    private static final Logger LOG = LoggerFactory.getLogger(IntermediaryService.class);

    private final DeviceRepository deviceRepository;

    private final GenMessagingClientFactory messagingClientFactory;

    private final GenHttpRequestFactory httpRequestFactory;

    private final ModbusGatewayFactory modbusGatewayFactory;

    private final ModbusGatewayRegistry sharedModbusGatewayRegistry;

    private final Map<String, GenDeviceApi> deviceRegistry = new HashMap<>();

    private final Map<String, String> errorDeviceRegistry = new HashMap<>();

    public IntermediaryService(
        DeviceRepository deviceRepository,
        GenMessagingClientFactory messagingClientFactory,
        GenHttpRequestFactory httpRequestFactory,
        ModbusGatewayFactory modbusGatewayFactory
    ) {
        this.deviceRepository = deviceRepository;
        this.messagingClientFactory = messagingClientFactory;
        this.httpRequestFactory = httpRequestFactory;
        this.modbusGatewayFactory = modbusGatewayFactory;

        // should be a single instance
        this.sharedModbusGatewayRegistry = new SGrModbusGatewayRegistry(this.modbusGatewayFactory);
    }

    public void loadDevices() {

        var devices = deviceRepository.findAll();
        devices.forEach(this::loadDevice);
    }

    public void loadDevice(Device device) {

        Properties properties = new Properties();
        device.getConfigurationValues().forEach(configurationValue -> properties.put(configurationValue.getName(), configurationValue.getVal()));

        try {
            var deviceInstance = new SGrDeviceBuilder()
                    .properties(properties)
                    .eid(device.getEiXml().getXml())
                    .useMessagingClientFactory(messagingClientFactory)
                    .useRestServiceClientFactory(httpRequestFactory)
                    .useModbusGatewayFactory(modbusGatewayFactory)
                    .useSharedModbusGatewayRegistry(sharedModbusGatewayRegistry)
                    .build();

            deviceInstance.connect();

            deviceRegistry.put(device.getName(), deviceInstance);
            LOG.info("Successfully loaded device named '{}' with EI-XML {}.", device.getName(), device.getEiXml().getName());
        } catch (GenDriverException | RestApiAuthenticationException e) {
            LOG.error("Failed to load device named {} with EI-XML {}. Error: {}", device.getName(),  device.getEiXml().getName(), e.getMessage());
            errorDeviceRegistry.put(device.getName(), e.getMessage());
        }
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

    @SuppressWarnings("unused")
    @PreDestroy
    void beforeTermination() {
        deviceRegistry.forEach( (deviceName, deviceApi) -> {
            try {
                deviceApi.disconnect();
                LOG.info("Successfully closed device: {}", deviceName);
            } catch (Exception e) {
                LOG.info("Failed to disconnect device: {}, {}", deviceName, e.getMessage());
            }
        } );
    }
}
