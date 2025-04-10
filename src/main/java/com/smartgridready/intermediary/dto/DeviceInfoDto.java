/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

/**
 * DTO for device info.
 */
public record DeviceInfoDto(
    String deviceName,
    String deviceCategory,
    String manufacturerName,
    List<FunctionalProfileDto> functionalProfiles
) {}
