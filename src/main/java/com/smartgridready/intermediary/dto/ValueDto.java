/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO for value.
 */
@Schema(description = "Represents a data point value")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValueDto
{
    @Schema(description = "The value")
    private Object value;
}
