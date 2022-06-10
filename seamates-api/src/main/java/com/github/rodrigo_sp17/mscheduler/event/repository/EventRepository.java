package com.github.rodrigo_sp17.mscheduler.event.repository;

import com.github.rodrigo_sp17.mscheduler.event.data.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select e from Event e where e.owner.userInfo.username = :ownerUsername " +
            "and e.id = :eventId")
    Optional<Event> findByIdAndOwnerUsername(Long eventId, String ownerUsername);

    @EntityGraph(value = "Event.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select e from Event e where e.owner.userInfo.username = :username")
    List<Event> findByOwnerUsername(String username);

}
