package ch.smartgridready.intermediary;


import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final ExternalInterfaceXmlRepository eiXmlRepository;

    private final ConfigurationValueRepository configurationValueRepository;

    public DeviceController(DeviceRepository deviceRepository,
                            ExternalInterfaceXmlRepository eiXmlRepository,
                            ConfigurationValueRepository configurationValueRepository) {
        this.deviceRepository = deviceRepository;
        this.eiXmlRepository = eiXmlRepository;
        this.configurationValueRepository = configurationValueRepository;
    }

    @PostMapping("/device")
    DeviceDto insertOrUpdateDevice(@RequestBody DeviceDto deviceDto) {

        var eiXml = eiXmlRepository.findByName(deviceDto.getEiXmlName()).stream().findFirst().orElseThrow(
                () -> new ExtIfXmlNotFoundException("EI-XML with name '" + deviceDto.getEiXmlName() + "' not found")
        );

        var device = deviceRepository.findByName(deviceDto.getName()).stream().findFirst().orElseGet(Device::new);
        device.setName(deviceDto.getName());
        device.setEiXml(eiXml);

        device.getConfigurationValues().forEach(configurationValueRepository::delete);
        device.getConfigurationValues().addAll(deviceDto.getConfigurationValues());

        deviceRepository.save(device);

        return new DeviceDto(device.getName(), device.getEiXml() != null ? device.getEiXml().getName() : "-", device.getConfigurationValues());
    }

    @GetMapping("/device/{deviceName}")
    DeviceDto getDevice(@PathVariable("deviceName") String deviceName) {

        var device = deviceRepository.findByName(deviceName).stream().findFirst().orElseThrow(() -> new DeviceNotFoundException(deviceName));
        return new DeviceDto(device.getName(), device.getEiXml() != null ? device.getEiXml().getName() : "-", device.getConfigurationValues());
    }

    @DeleteMapping("/device/{deviceName}")
    void  deleteDevice (@PathVariable("deviceName") String deviceName) {

        var device = deviceRepository.findByName(deviceName).stream().findFirst().orElseThrow(() -> new DeviceNotFoundException(deviceName));
        device.getConfigurationValues().forEach(configurationValueRepository::delete);
        deviceRepository.delete(device);
    }

}
