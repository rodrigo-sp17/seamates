package com.github.rodrigo_sp17.mscheduler.friend.data;

import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class FriendRequest {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AppUser source;

    @ManyToOne
    private AppUser target;

    private LocalDateTime timestamp;
}
