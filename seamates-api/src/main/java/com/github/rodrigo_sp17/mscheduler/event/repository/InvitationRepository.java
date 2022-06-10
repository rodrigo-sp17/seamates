package com.github.rodrigo_sp17.mscheduler.event.repository;

import com.github.rodrigo_sp17.mscheduler.event.data.Invitation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    @Query("select i from Invitation i where i.event.id = :eventId " +
            "and i.event.owner.userInfo.username = :ownerUsername " +
            "and i.invited.userInfo.username = :invitedUsername")
    Optional<Invitation> findByEventIdAndOwnerUsernameAndInvitedUsername(Long eventId,
                                                                         String ownerUsername,
                                                                         String invitedUsername);

    @Query("select i from Invitation i where i.event.id = :eventId " +
            "and i.event.owner.userInfo.username = :ownerUsername")
    List<Invitation> findByEventIdAndOwnerUsername(Long eventId, String ownerUsername);

    @Query("select i from Invitation i where i.event.id = :eventId " +
            "and i.invited.userInfo.username = :invitedUsername")
    Optional<Invitation> findByEventIdAndInvitedUsername(Long eventId, String invitedUsername);

    @Query("select case when count(i)>0 then true else false end from Invitation i where i.event.id = :eventId " +
            "and i.invited.userInfo.username = :invitedUsername")
    Boolean existsByEventIdAndInvitedUsername(Long eventId, String invitedUsername);

    @EntityGraph(value = "Invitation.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select i from Invitation i where i.invited.userInfo.username = :username")
    List<Invitation> findInvitationsByInvitedUsername(String username);

}
