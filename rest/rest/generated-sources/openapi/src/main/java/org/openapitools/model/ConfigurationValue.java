package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConfigurationValue
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-09-20T17:51:07.994232+02:00[Europe/Zurich]", comments = "Generator version: 7.8.0")
public class ConfigurationValue {

  private Long id;

  private String name;

  private String val;

  public ConfigurationValue id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ConfigurationValue name(String name) {
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

  public ConfigurationValue val(String val) {
    this.val = val;
    return this;
  }

  /**
   * Get val
   * @return val
   */
  
  @Schema(name = "val", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("val")
  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfigurationValue configurationValue = (ConfigurationValue) o;
    return Objects.equals(this.id, configurationValue.id) &&
        Objects.equals(this.name, configurationValue.name) &&
        Objects.equals(this.val, configurationValue.val);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, val);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigurationValue {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    val: ").append(toIndentedString(val)).append("\n");
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

