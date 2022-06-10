package com.github.rodrigo_sp17.mscheduler.event;

import com.github.rodrigo_sp17.mscheduler.event.data.Invitation;
import com.github.rodrigo_sp17.mscheduler.event.exception.InvitationNotFoundException;
import com.github.rodrigo_sp17.mscheduler.event.repository.InvitationRepository;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvitationService {
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    public boolean existsInvitation(Long eventId, String invitedUsername) {
        return invitationRepository.existsByEventIdAndInvitedUsername(eventId, invitedUsername);
    }

    public List<Invitation> getInvitationsForEvent(Long eventId, String ownerUsername) {
        return invitationRepository.findByEventIdAndOwnerUsername(eventId, ownerUsername);
    }

    // Only INVITED
    @Transactional
    public Invitation confirmPresence(Long eventId, String invitedUsername) {
        var invitation = invitationRepository
                .findByEventIdAndInvitedUsername(eventId, invitedUsername)
                .orElseThrow(InvitationNotFoundException::new);

        invitation.setConfirmed(true);
        return invitationRepository.save(invitation);
    }

    // Only OWNER
    @Transactional
    public Invitation inviteNewUser(Long eventId,
                                    String invitedUsername,
                                    String ownerUsername) {
        var invited = userService.getUserByUsername(invitedUsername);
        var event = eventService.getEvent(eventId, ownerUsername);

        var invitation = new Invitation();
        invitation.setEvent(event);
        invitation.setInvited(invited);
        invitation.setConfirmed(false);

        return invitationRepository.save(invitation);
    }

    // Only OWNER
    @Transactional
    public void removeInvitation(Long eventId,
                                 String invitedUsername,
                                 String ownerUsername) {
        var invitation = invitationRepository
                .findByEventIdAndOwnerUsernameAndInvitedUsername(eventId, ownerUsername, invitedUsername)
                .orElseThrow(InvitationNotFoundException::new);
        invitationRepository.delete(invitation);
    }
}
