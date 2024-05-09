package ch.smartgridready.intermediary.dto;

import ch.smartgridready.intermediary.entity.ConfigurationValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private String name;
    private String eiXmlName;
    private List<ConfigurationValue> configurationValues;
    private String status;
}
