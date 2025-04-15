/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents functional profile's data point.")
public record DataPointDto(
    @Schema(description = "The data point name, defined by EID")
    String dataPointName,

    @Schema(description = "The data point unit")
    String unit,

    @Schema(description = "The data point value data type")
    String dataType,

    @Schema(description = "The allowed values of the data type (min./max. value for numeric types, a list of allowed values for enum or bitmap types)")
    List<String> dataTypeValues,

    @Schema(description = "The data point minimal value (optional)")
    Double minValue,

    @Schema(description = "The data point maximal value (optional)")
    Double maxValue,

    @Schema(description = "The data point array size (optional, only defined when > 0)")
    Integer arrayLength,

    @Schema(description = "True if the datapoint can be read, otherwise false.")
    boolean isRead,

    @Schema(description = "True if the datapoint can be written, otherwise false.")
    boolean isWrite,

    @Schema(description = "True if the datapoint is persistent, otherwise false.")
    boolean isPersistent,

    @Schema(description = "The generic attributes, defined by EID")
    List<GenericAttributeDto> genericAttributes,

    @Schema(description = "The dynamic parameters, defined by EID")
    List<DynamicParameterDto> dynamicParameters
) {}
