package com.github.rodrigo_sp17.mscheduler.push.events;

import java.util.HashMap;

public class FriendAcceptEvent extends PushEvent {
    public FriendAcceptEvent(String sourceUsername, String targetUsername) {
        super("FRIEND_ACCEPT", new HashMap<>());
        this.getBody().put("source", sourceUsername);
        this.getBody().put("target", targetUsername);
    }
}
