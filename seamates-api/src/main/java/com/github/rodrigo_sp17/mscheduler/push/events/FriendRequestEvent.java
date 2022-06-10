package com.github.rodrigo_sp17.mscheduler.push.events;

import java.util.HashMap;

public class FriendRequestEvent extends PushEvent {
    public FriendRequestEvent(String sourceUsername, String targetUsername) {
        super("FRIEND_REQUEST", new HashMap<>());
        this.getBody().put("source", sourceUsername);
        this.getBody().put("target", targetUsername);
    }
}
