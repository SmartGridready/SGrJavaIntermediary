/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a dynamic parameter of a data point.")
public record DynamicParameterDto(
    @Schema(description = "The parameter name")
    String name,

    @Schema(description = "The parameter data type")
    String dataType,

    @Schema(description = "The allowed values of the data type (min./max. value for numeric types, a list of allowed values for enum or bitmap types)")
    List<String> dataTypeValues,

    @Schema(description = "The default parameter value")
    String defaultValue
) {}
