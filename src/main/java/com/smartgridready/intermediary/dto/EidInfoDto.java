/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents information of an EI-XML.")
public record EidInfoDto(
    @Schema(description = "The EI-XML identifier")
    String identifier,

    @Schema(description = "The EI-XML version")
    String version,

    @Schema(description = "The allowed values of the data type (min./max. value for numeric types, a list of allowed values for enum or bitmap types)")
    List<ParameterDto> configurationParameters
) {}
