package com.github.rodrigo_sp17.mscheduler.event.data;

import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NamedEntityGraph(name = "Invitation.detail", attributeNodes = {
        @NamedAttributeNode("event")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser invited;

    private boolean isConfirmed;
}
