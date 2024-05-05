package ch.smartgridready.intermediary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DeviceDto {
    private String name;
    private String eiXmlName;
    private List<ConfigurationValue> configurationValues;
}
