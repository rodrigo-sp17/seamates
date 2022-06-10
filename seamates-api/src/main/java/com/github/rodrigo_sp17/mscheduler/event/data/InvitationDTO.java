package com.github.rodrigo_sp17.mscheduler.event.data;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class InvitationDTO extends RepresentationModel<InvitationDTO> {
    private Long id;
    private Long eventId;
    private String invitedUsername;
    private boolean isConfirmed;
}
