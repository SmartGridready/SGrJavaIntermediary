package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.ConfigurationValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DeviceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-09-20T17:51:07.994232+02:00[Europe/Zurich]", comments = "Generator version: 7.8.0")
public class DeviceDto {

  private String name;

  private String eiXmlName;

  @Valid
  private List<@Valid ConfigurationValue> configurationValues = new ArrayList<>();

  private String status;

  public DeviceDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DeviceDto eiXmlName(String eiXmlName) {
    this.eiXmlName = eiXmlName;
    return this;
  }

  /**
   * Get eiXmlName
   * @return eiXmlName
   */
  
  @Schema(name = "eiXmlName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eiXmlName")
  public String getEiXmlName() {
    return eiXmlName;
  }

  public void setEiXmlName(String eiXmlName) {
    this.eiXmlName = eiXmlName;
  }

  public DeviceDto configurationValues(List<@Valid ConfigurationValue> configurationValues) {
    this.configurationValues = configurationValues;
    return this;
  }

  public DeviceDto addConfigurationValuesItem(ConfigurationValue configurationValuesItem) {
    if (this.configurationValues == null) {
      this.configurationValues = new ArrayList<>();
    }
    this.configurationValues.add(configurationValuesItem);
    return this;
  }

  /**
   * Get configurationValues
   * @return configurationValues
   */
  @Valid 
  @Schema(name = "configurationValues", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("configurationValues")
  public List<@Valid ConfigurationValue> getConfigurationValues() {
    return configurationValues;
  }

  public void setConfigurationValues(List<@Valid ConfigurationValue> configurationValues) {
    this.configurationValues = configurationValues;
  }

  public DeviceDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeviceDto deviceDto = (DeviceDto) o;
    return Objects.equals(this.name, deviceDto.name) &&
        Objects.equals(this.eiXmlName, deviceDto.eiXmlName) &&
        Objects.equals(this.configurationValues, deviceDto.configurationValues) &&
        Objects.equals(this.status, deviceDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, eiXmlName, configurationValues, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeviceDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    eiXmlName: ").append(toIndentedString(eiXmlName)).append("\n");
    sb.append("    configurationValues: ").append(toIndentedString(configurationValues)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

