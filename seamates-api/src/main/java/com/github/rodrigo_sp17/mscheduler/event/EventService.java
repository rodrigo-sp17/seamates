package com.github.rodrigo_sp17.mscheduler.event;

import com.github.rodrigo_sp17.mscheduler.event.data.Event;
import com.github.rodrigo_sp17.mscheduler.event.data.Invitation;
import com.github.rodrigo_sp17.mscheduler.event.exception.EventNotFoundException;
import com.github.rodrigo_sp17.mscheduler.event.repository.EventRepository;
import com.github.rodrigo_sp17.mscheduler.event.repository.InvitationRepository;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserService userService;

    /**
     * Gets a Event object from the repository
     * @param eventId the id of the Event
     * @return the Event object with the eventId
     * @throws EventNotFoundException if the id does not exist in the repository
     */
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
    }

    /**
     * Gets a Event object from the repository
     * @param eventId the id of the Event
     * @param ownerUsername the username of the Event owner
     * @return the Event with the provided restrictions
     * @throws EventNotFoundException if no Event matches both the id and the owner username
     */
    public Event getEvent(Long eventId, String ownerUsername) {
        return eventRepository.findByIdAndOwnerUsername(eventId, ownerUsername)
                .orElseThrow(EventNotFoundException::new);
    }

    public List<Event> getAllEvents(String username) {
        var owned = eventRepository.findByOwnerUsername(username);
        var invited = invitationRepository.findInvitationsByInvitedUsername(username)
                .stream().map(Invitation::getEvent).collect(Collectors.toList());
        owned.addAll(invited);
        return owned;
    }

    public List<Event> getOwnedEvents(String username) {
        return eventRepository.findByOwnerUsername(username);
    }

    @Transactional
    public Event createEvent(Event event,
                             String ownerUsername,
                             Collection<String> invitedUsernames) {
        var sanitized = sanitizeDates(event);

        // Ensures a edition will not be made
        sanitized.setId(null);

        var owner = userService.getUserByUsername(ownerUsername);
        sanitized.setOwner(owner);

        var savedEvent = eventRepository.save(event);
        savedEvent.setOwner(owner);

        var invited = invitedUsernames.stream()
                .map(name -> userService.getUserByUsername(name))
                .map(u -> new Invitation(null, savedEvent, u, false))
                .collect(Collectors.toList());

        invitationRepository.saveAll(invited);
        return savedEvent;
    }

    @Transactional
    public Event editEvent(Event editedEvent,
                           String ownerUsername) {
        // If its not the owner, exception will be thrown here - no need for double checking ownership
        var eventToEdit = getEvent(editedEvent.getId(), ownerUsername);

        if (editedEvent.getTitle() != null) {
            eventToEdit.setTitle(editedEvent.getTitle());
        }
        if (editedEvent.getStart() != null) {
            eventToEdit.setStart(editedEvent.getStart());
        }
        if (editedEvent.getEnding() != null) {
            eventToEdit.setEnding(editedEvent.getEnding());
        }
        eventToEdit.setAllDay(editedEvent.isAllDay());

        var sanitized = sanitizeDates(eventToEdit);
        return eventRepository.save(sanitized);
    }

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        var eventToDelete = getEvent(eventId, username);
        eventRepository.delete(eventToDelete);
    }



    /* Private methods ************************************************************************************/

    /**
     * Returns a Event object with sanitized start and end dates
     * @param event the Event object to sanitize
     * @return the Event object with properly validated dates
     * @throws IllegalArgumentException if the provided Event has non-valid data
     */
    private Event sanitizeDates(Event event) {
        var sanitized = new Event();
        BeanUtils.copyProperties(event, sanitized);

        if (event.getEnding() == null) {
            event.setAllDay(true);
        }

        if (event.isAllDay()) {
            sanitized.setEnding(event.getStart());
        } else if (event.getEnding().isBefore(event.getStart())) {
            throw new IllegalArgumentException("Event can't end before it began");
        }
        return sanitized;
    }

}
