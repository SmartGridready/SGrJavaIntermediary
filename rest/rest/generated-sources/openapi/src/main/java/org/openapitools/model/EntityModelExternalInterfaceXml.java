package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.model.Link;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EntityModelExternalInterfaceXml
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-09-20T17:51:07.994232+02:00[Europe/Zurich]", comments = "Generator version: 7.8.0")
public class EntityModelExternalInterfaceXml {

  private Long id;

  private String name;

  private String xml;

  @Valid
  private Map<String, Link> links = new HashMap<>();

  public EntityModelExternalInterfaceXml id(Long id) {
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

  public EntityModelExternalInterfaceXml name(String name) {
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

  public EntityModelExternalInterfaceXml xml(String xml) {
    this.xml = xml;
    return this;
  }

  /**
   * Get xml
   * @return xml
   */
  
  @Schema(name = "xml", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("xml")
  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public EntityModelExternalInterfaceXml links(Map<String, Link> links) {
    this.links = links;
    return this;
  }

  public EntityModelExternalInterfaceXml putLinksItem(String key, Link linksItem) {
    if (this.links == null) {
      this.links = new HashMap<>();
    }
    this.links.put(key, linksItem);
    return this;
  }

  /**
   * Get links
   * @return links
   */
  @Valid 
  @Schema(name = "_links", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("_links")
  public Map<String, Link> getLinks() {
    return links;
  }

  public void setLinks(Map<String, Link> links) {
    this.links = links;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityModelExternalInterfaceXml entityModelExternalInterfaceXml = (EntityModelExternalInterfaceXml) o;
    return Objects.equals(this.id, entityModelExternalInterfaceXml.id) &&
        Objects.equals(this.name, entityModelExternalInterfaceXml.name) &&
        Objects.equals(this.xml, entityModelExternalInterfaceXml.xml) &&
        Objects.equals(this.links, entityModelExternalInterfaceXml.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, xml, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityModelExternalInterfaceXml {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    xml: ").append(toIndentedString(xml)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

