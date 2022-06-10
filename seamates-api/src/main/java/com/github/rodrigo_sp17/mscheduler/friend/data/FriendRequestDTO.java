package com.github.rodrigo_sp17.mscheduler.friend.data;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class FriendRequestDTO extends RepresentationModel<FriendRequestDTO> {
    private Long id;
    private String sourceUsername;
    private String sourceName;
    private String targetUsername;
    private String targetName;
    private LocalDateTime timestamp;
}
