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

@Schema(description = "Represents an error response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto
{
    @Schema(description = "The class name where the error occurred")
    private String className;

    @Schema(description = "The error message")
    private String errorMsg;
}
