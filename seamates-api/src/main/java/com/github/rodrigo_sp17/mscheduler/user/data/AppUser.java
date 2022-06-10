package com.github.rodrigo_sp17.mscheduler.user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredential;
import com.github.rodrigo_sp17.mscheduler.event.data.Event;
import com.github.rodrigo_sp17.mscheduler.event.data.Invitation;
import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class AppUser extends RepresentationModel<AppUser> {

    @Id
    @GeneratedValue
    private Long userId;

    @Embedded
    private UserInfo userInfo;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JsonIgnore
    private List<AppUser> friends;

    @ToString.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Shift> shifts;

    @ToString.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Event> ownedEvents;

    @ToString.Exclude
    @OneToMany(mappedBy = "invited")
    private List<Invitation> invitations;

    @JsonIgnore
    @OneToMany(mappedBy = "socialUser", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SocialCredential> credentials;
}
