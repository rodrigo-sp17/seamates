package com.github.rodrigo_sp17.mscheduler.shift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShiftNotFoundException extends RuntimeException{

    public ShiftNotFoundException() {
    }

    public ShiftNotFoundException(String message) {
        super(message);
    }
}
