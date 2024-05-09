package ch.smartgridready.intermediary.controller;


import ch.smartgridready.intermediary.dto.DeviceDto;
import ch.smartgridready.intermediary.entity.Device;
import ch.smartgridready.intermediary.exception.DeviceNotFoundException;
import ch.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import ch.smartgridready.intermediary.repository.ConfigurationValueRepository;
import ch.smartgridready.intermediary.repository.DeviceRepository;
import ch.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import ch.smartgridready.intermediary.service.IntermediaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final ExternalInterfaceXmlRepository eiXmlRepository;

    private final ConfigurationValueRepository configurationValueRepository;

    private final IntermediaryService intermediaryService;

    public DeviceController(DeviceRepository deviceRepository,
                            ExternalInterfaceXmlRepository eiXmlRepository,
                            ConfigurationValueRepository configurationValueRepository,
                            IntermediaryService intermediaryService) {
        this.deviceRepository = deviceRepository;
        this.eiXmlRepository = eiXmlRepository;
        this.configurationValueRepository = configurationValueRepository;
        this.intermediaryService = intermediaryService;
    }

    @PostMapping("/device")
    DeviceDto insertOrUpdateDevice(@RequestBody DeviceDto deviceDto) {

        var eiXml = eiXmlRepository.findByName(deviceDto.getEiXmlName()).stream().findFirst().orElseThrow(
                () -> new ExtIfXmlNotFoundException("EI-XML with name '" + deviceDto.getEiXmlName() + "' not found")
        );

        var device = deviceRepository.findByName(deviceDto.getName()).stream().findFirst().orElseGet(Device::new);
        device.setName(deviceDto.getName());
        device.setEiXml(eiXml);

        device.getConfigurationValues().clear();
        deviceRepository.save(device);

        deviceDto.getConfigurationValues().forEach(configValue -> {
            configValue.setDevice(device);
            device.getConfigurationValues().add(configValue);
        });

        deviceRepository.save(device);

        intermediaryService.loadDevices();

        return new DeviceDto(
                device.getName(),
                device.getEiXml() != null ? device.getEiXml().getName() : "-",
                device.getConfigurationValues(),
                intermediaryService.getDeviceStatus(device.getName()));
    }

    @GetMapping("/device/{deviceName}")
    DeviceDto getDevice(@PathVariable("deviceName") String deviceName) {

        var device = deviceRepository.findByName(deviceName).stream().findFirst().orElseThrow(() -> new DeviceNotFoundException(deviceName));
        return new DeviceDto(
                device.getName(),
                device.getEiXml() != null ? device.getEiXml().getName() : "-",
                device.getConfigurationValues(),
                intermediaryService.getDeviceStatus(deviceName));
    }

    @GetMapping("/device")
    List<DeviceDto> getAll() {
        var devices = deviceRepository.findAll();
        return devices.stream()
                .map(device -> new DeviceDto(
                        device.getName(),
                        device.getEiXml() != null ? device.getEiXml().getName() : "-",
                        device.getConfigurationValues(),
                        intermediaryService.getDeviceStatus(device.getName()
                ))).toList();
    }

    @DeleteMapping("/device/{deviceName}")
    void  deleteDevice (@PathVariable("deviceName") String deviceName) {

        var device = deviceRepository.findByName(deviceName).stream().findFirst().orElseThrow(() -> new DeviceNotFoundException(deviceName));
        device.getConfigurationValues().forEach(configurationValueRepository::delete);
        deviceRepository.delete(device);
    }
}
