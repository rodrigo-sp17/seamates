package com.github.rodrigo_sp17.mscheduler.event;

import com.github.rodrigo_sp17.mscheduler.event.data.Event;
import com.github.rodrigo_sp17.mscheduler.event.data.EventDTO;
import com.github.rodrigo_sp17.mscheduler.event.data.Invitation;
import com.github.rodrigo_sp17.mscheduler.event.data.InvitationDTO;
import com.github.rodrigo_sp17.mscheduler.event.exception.InvitationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/event")
public class EventController {

    private final Link ALL_LINK = linkTo(methodOn(EventController.class)
            .getAllEvents(null)).withRel("all_events");

    @Autowired
    private EventService eventService;
    @Autowired
    private InvitationService invitationService;


    @GetMapping
    public ResponseEntity<CollectionModel<EventDTO>> getAllEvents(Authentication auth) {
        var events = eventService.getAllEvents(auth.getName());
        var dtos = events.stream()
                .map(this::getDtoFromEvent)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(dtos).add(ALL_LINK));
    }

    @GetMapping("/owned")
    public ResponseEntity<CollectionModel<EventDTO>> getOwnedEvents(Authentication auth) {
        var events = eventService.getOwnedEvents(auth.getName());
        var dtos = events.stream()
                .map(this::getDtoFromEvent)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(dtos).add(ALL_LINK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id,
                                                 Authentication auth) {
        // Only returns the event if its either the owner or one of the invited
        var event = eventService.getEvent(id);
        if (!event.getOwner().getUserInfo().getUsername().equals(auth.getName())) {
            if (!invitationService.existsInvitation(id, auth.getName())) {
                throw new InvitationNotFoundException();
            }
        }

        var dto = getDtoFromEvent(event);
        var invitations = invitationService.getInvitationsForEvent(id, event.getOwner()
                .getUserInfo().getUsername());
        dto.setInvitedUsernames(new HashSet<>());
        dto.setConfirmedUsernames(new HashSet<>());

        invitations.forEach(i -> {
            var username = i.getInvited().getUserInfo().getUsername();
            if (i.isConfirmed()) {
                dto.getConfirmedUsernames().add(username);
            } else {
                dto.getInvitedUsernames().add(username);
            }
        });

        return ResponseEntity.ok(dto.add(ALL_LINK));
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO dto,
                                             Authentication auth) {
        var createdEvent = eventService.createEvent(getEventFromDto(dto),
                auth.getName(),
                dto.getInvitedUsernames());
        log.info("Created event");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getDtoFromEvent(createdEvent).add(ALL_LINK));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventDTO> editEventDetails(@PathVariable Long id,
                                                     @RequestBody EventDTO dto,
                                                     Authentication auth) {
        var event = getEventFromDto(dto);
        event.setId(id);

        var editedEvent = eventService.editEvent(getEventFromDto(dto),
                auth.getName());
        return ResponseEntity.ok(getDtoFromEvent(editedEvent).add(ALL_LINK));
    }

    @PostMapping("/{id}/invite/{username}")
    public ResponseEntity<InvitationDTO> inviteToEvent(@PathVariable Long id,
                                                       @PathVariable String username,
                                                       Authentication auth) {
        var invitation = invitationService.inviteNewUser(id,
                username, auth.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getDtoFromInvitation(invitation));
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<InvitationDTO> confirmEvent(@PathVariable Long id,
                                                 Authentication auth) {
        var invitation = invitationService.confirmPresence(id, auth.getName());
        return ResponseEntity.ok(getDtoFromInvitation(invitation));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/invite/{username}")
    public void uninvite(@PathVariable Long id,
                         @PathVariable String username,
                         Authentication auth) {
        invitationService.removeInvitation(id, username, auth.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id, Authentication auth) {
        eventService.deleteEvent(id, auth.getName());
        log.info("Deleted event");
    }



    /* Private methods ***************************************************************/

    private EventDTO getDtoFromEvent(Event event) {
        var dto = new EventDTO();
        BeanUtils.copyProperties(event, dto);
        dto.setOwnerUsername(event.getOwner().getUserInfo().getUsername());
        var self = linkTo(methodOn(EventController.class)
                .getEventById(event.getId(), null)).withSelfRel();

        return dto.add(self);
    }

    /**
     * Gets a Event object from the dto.
     * Does not convert usernames to AppUsers.
     * @param dto the dto to convert
     * @return the Event object, without AppUsers
     */
    private Event getEventFromDto(EventDTO dto) {
        var event = new Event();
        BeanUtils.copyProperties(dto, event);
        return event;
    }

    private InvitationDTO getDtoFromInvitation(Invitation invitation) {
        var dto = new InvitationDTO();
        dto.setId(invitation.getId());
        dto.setConfirmed(invitation.isConfirmed());
        dto.setInvitedUsername(invitation
                .getInvited().getUserInfo().getUsername());
        dto.setEventId(invitation.getEvent().getId());
        var self = linkTo(methodOn(EventController.class)
                .getEventById(invitation.getEvent().getId(), null))
                .withSelfRel();
        return dto.add(self);
    }
}
