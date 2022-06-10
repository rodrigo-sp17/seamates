package com.github.rodrigo_sp17.mscheduler.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            @RequestHeader(value = "Authorization") String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No token detected");
        }
        var token = bearerToken.replace("Bearer ", "");
        var refreshedToken = authenticationService.refreshToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"));
        return ResponseEntity.ok().header("Authorization", "Bearer " + refreshedToken).build();
    }

}
