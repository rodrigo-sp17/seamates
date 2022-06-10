package com.github.rodrigo_sp17.mscheduler.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/push")
public class PushController {
    @Autowired
    private PushService pushService;

    @GetMapping("/subscribe/{username}")
    public SseEmitter streamEvents(@PathVariable("username") String username,
                                                         Authentication auth) {
        var authUsername = auth.getName();
        if (!authUsername.equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Subscription unauthorized");
        }
        return pushService.subscribe(authUsername);
    }
}
