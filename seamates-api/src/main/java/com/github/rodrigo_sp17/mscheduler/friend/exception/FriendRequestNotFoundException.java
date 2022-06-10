package com.github.rodrigo_sp17.mscheduler.friend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FriendRequestNotFoundException extends RuntimeException {

    public FriendRequestNotFoundException() {
    }

    public FriendRequestNotFoundException(String message) {
        super(message);
    }
}
