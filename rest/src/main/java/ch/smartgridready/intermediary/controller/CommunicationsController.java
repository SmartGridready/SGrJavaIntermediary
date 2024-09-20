package ch.smartgridready.intermediary.controller;

import ch.smartgridready.intermediary.dto.ValueDto;
import ch.smartgridready.intermediary.exception.DeviceOperationFailedException;
import ch.smartgridready.intermediary.service.IntermediaryService;
import communicator.common.api.values.Float64Value;
import communicator.common.api.values.StringValue;
import communicator.common.api.values.Value;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class CommunicationsController {

    private final IntermediaryService intermediaryService;

    @GetMapping("/value/{device}/{functionalProfile}/{dataPoint}")
    public String getVal(
            @PathVariable("device") String device,
            @PathVariable("functionalProfile") String functionalProfile,
            @PathVariable("dataPoint") String dataPoint) throws DeviceOperationFailedException {

            try {
                return intermediaryService.getVal(device, functionalProfile, dataPoint).getString();
            } catch (Exception e) {
                return e.getMessage();
            }
    }

    @PostMapping("/value/{device}/{functionalProfile}/{dataPoint}")
    public void setVal(
            @PathVariable("device") String device,
            @PathVariable("functionalProfile") String functionalProfile,
            @PathVariable("dataPoint") String dataPoint,
            @RequestBody ValueDto value) throws DeviceOperationFailedException {

        Value sgrValue;
        if (value.getValue() instanceof Number) {
            sgrValue = Float64Value.of((Double) value.getValue());
        } else {
            sgrValue = StringValue.of((String) value.getValue());
        }

        intermediaryService.setVal(device, functionalProfile, dataPoint, sgrValue);
    }
}
