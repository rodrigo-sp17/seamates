package com.github.rodrigo_sp17.mscheduler.event.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO extends RepresentationModel<EventDTO> {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 60)
    private String title;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean allDay;

    private String ownerUsername;
    private Set<String> invitedUsernames;
    private Set<String> confirmedUsernames;
}
