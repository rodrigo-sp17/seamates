package com.github.rodrigo_sp17.mscheduler.event.data;

import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NamedEntityGraph(name = "Event.detail",
attributeNodes = @NamedAttributeNode("owner"))
@Data
public class Event {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime ending;
    private boolean allDay;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppUser owner;
 }
