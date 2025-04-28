/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for device info.
 */
@Schema(description = "Represents device information for introspection")
public record DeviceInfoDto(
    @Schema(description = "The device name")
    String deviceName,
    @Schema(description = "The device category")
    String deviceCategory,
    @Schema(description = "The manufacturer name")
    String manufacturerName,
    @Schema(description = "The functional profiles")
    List<FunctionalProfileDto> functionalProfiles
) {}
