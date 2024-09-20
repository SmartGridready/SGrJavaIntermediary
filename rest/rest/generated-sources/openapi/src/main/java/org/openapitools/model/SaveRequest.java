package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SaveRequest
 */

@JsonTypeName("save_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-09-20T17:51:07.994232+02:00[Europe/Zurich]", comments = "Generator version: 7.8.0")
public class SaveRequest {

  private org.springframework.core.io.Resource file;

  public SaveRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SaveRequest(org.springframework.core.io.Resource file) {
    this.file = file;
  }

  public SaveRequest file(org.springframework.core.io.Resource file) {
    this.file = file;
    return this;
  }

  /**
   * Get file
   * @return file
   */
  @NotNull @Valid 
  @Schema(name = "file", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("file")
  public org.springframework.core.io.Resource getFile() {
    return file;
  }

  public void setFile(org.springframework.core.io.Resource file) {
    this.file = file;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaveRequest saveRequest = (SaveRequest) o;
    return Objects.equals(this.file, saveRequest.file);
  }

  @Override
  public int hashCode() {
    return Objects.hash(file);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SaveRequest {\n");
    sb.append("    file: ").append(toIndentedString(file)).append("\n");
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

