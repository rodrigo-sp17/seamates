package com.github.rodrigo_sp17.mscheduler.shift.data;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * This class represents a Shift to be added or edited
 */
@Data
public class ShiftRequestDTO extends RepresentationModel<ShiftRequestDTO> {

    private Long shiftId;
    private LocalDate unavailabilityStartDate;
    @NotNull
    private LocalDate boardingDate;
    private LocalDate leavingDate;
    private LocalDate unavailabilityEndDate;

    // Regular number of shift days to use for calculations
    // Usage: if you only want to provide boarding date
    // i.e: 14, 28, 35, 56. 0 == null.
    private Integer cycleDays;

    // Times to repeat the schedule
    private Integer repeat;

}
