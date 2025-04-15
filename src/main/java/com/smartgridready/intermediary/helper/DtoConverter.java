/*
 * Copyright(c) 2025 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.helper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.smartgridready.intermediary.dto.DataPointDto;
import com.smartgridready.intermediary.dto.DeviceInfoDto;
import com.smartgridready.intermediary.dto.DynamicParameterDto;
import com.smartgridready.intermediary.dto.FunctionalProfileDto;
import com.smartgridready.intermediary.dto.GenericAttributeDto;
import com.smartgridready.ns.v0.DataDirectionProduct;
import com.smartgridready.communicator.common.api.dto.DataPoint;
import com.smartgridready.communicator.common.api.dto.DeviceInfo;
import com.smartgridready.communicator.common.api.dto.DynamicRequestParameter;
import com.smartgridready.communicator.common.api.dto.FunctionalProfile;
import com.smartgridready.communicator.common.api.dto.GenericAttribute;
import com.smartgridready.communicator.common.api.values.Value;

public class DtoConverter {

    // hide constructor
    private DtoConverter() {}

    public static DeviceInfoDto deviceInfoDto(DeviceInfo info, List<FunctionalProfile> functionalProfiles) {
        return new DeviceInfoDto(
            info.getName(),
            info.getDeviceCategory().value(),
            info.getManufacturer(),
            functionalProfiles.stream().map(DtoConverter::functionalProfileDto).collect(Collectors.toList())
        );
    }

    public static FunctionalProfileDto functionalProfileDto(FunctionalProfile fp) {
        return new FunctionalProfileDto(
            fp.getName(),
            fp.getProfileType(),
            fp.getCategory().value(),
            fp.getDataPoints().stream().map(DtoConverter::dataPointDto).collect(Collectors.toList()),
            genericAttributeDtos(fp.getGenericAttributes())
        );
    }

    public static DataPointDto dataPointDto(DataPoint dp) {
        return new DataPointDto(
            dp.getName(),
            dp.getUnit().value(),
            dp.getDataType().getTypeName(),
            getRange(dp.getDataType().getRange()),
            dp.getMinimumValue(),
            dp.getMaximumValue(),
            dp.getArrayLen(),
            isReadable(dp.getPermissions()),
            isWritable(dp.getPermissions()),
            isPersistent(dp.getPermissions()),
            genericAttributeDtos(dp.getGenericAttributes()),
            dynamicParameterDtos(dp)
        );
    }

    public static DynamicParameterDto dynamicParameterDto(DynamicRequestParameter param) {
        return new DynamicParameterDto(
            param.getName(),
            param.getDataType().getTypeName(),
            getRange(param.getDataType().getRange()),
            param.getDefaultValue()
        );
    }

    private static List<GenericAttributeDto> genericAttributeDtos(List<GenericAttribute> ge) {
        return (ge != null)
            ? ge.stream()
                .map(g ->
                    new GenericAttributeDto(
                        g.getName(),
                        g.getValue().getString(),
                        g.getUnit().value(),
                        g.getDataType().getTypeName(),
                        genericAttributeDtos(g.getChildren())
                    )
                )
                .collect(Collectors.toList())
            : null;
    }

    private static List<DynamicParameterDto> dynamicParameterDtos(DataPoint dp) {
        return dp.getDynamicRequestParameters().stream().map(DtoConverter::dynamicParameterDto).collect(Collectors.toList());
    }

    private static List<String> getRange(List<Value> values) {
        if (values == null) {
            return Collections.emptyList();
        }

        return values.stream().map(Value::getString).collect(Collectors.toList());
    }

    private static boolean isReadable(DataDirectionProduct d) {
        return (
            d == DataDirectionProduct.R ||
            d == DataDirectionProduct.RW ||
            d == DataDirectionProduct.RWP ||
            d == DataDirectionProduct.C
        );
    }

    private static boolean isWritable(DataDirectionProduct d) {
        return (
            d == DataDirectionProduct.W ||
            d == DataDirectionProduct.RW ||
            d == DataDirectionProduct.RWP
        );
    }

    private static boolean isPersistent(DataDirectionProduct d) {
        return (
            d == DataDirectionProduct.RWP
        );
    }
}
