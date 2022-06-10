package com.github.rodrigo_sp17.mscheduler.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException() {
    }

    public InvitationNotFoundException(String message) {
        super(message);
    }
}
