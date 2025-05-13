/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a generic attribute of a data point.")
public record GenericAttributeDto(
    @Schema(description = "The attribute name")
    String name,

    @Schema(description = "The attribute value as string")
    String value,

    @Schema(description = "The attribute value unit")
    String unit,

    @Schema(description = "The attribute value data type")
    String dataType,

    @Schema(description = "The sub-attributes (optional)")
    List<GenericAttributeDto> children
) {}
