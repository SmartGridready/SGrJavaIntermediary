/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a functional profile.")
public record FunctionalProfileDto(
    @Schema(description = "The functional profile name, defined by EI-XML")
    String functionalProfileName,

    @Schema(description = "The functional profile type, defined by SGr standard")
    String functionalProfileType,

    @Schema(description = "The functional profile category, defined by SGr standard")
    String functionalProfileCategory,

    @Schema(description = "The data points, defined by EI-XML")
    List<DataPointDto> dataPoints,

    @Schema(description = "The generic attributes, defined by EI-XML")
    List<GenericAttributeDto> genericAttributes
) {}
