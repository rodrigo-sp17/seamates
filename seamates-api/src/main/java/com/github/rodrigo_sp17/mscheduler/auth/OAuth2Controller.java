package com.github.rodrigo_sp17.mscheduler.auth;

import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredentialRepository;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    private SocialCredentialRepository socialCredentialRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private OAuth2Service oAuth2Service;

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam("signed_request") String signedRequest) {
        var data = oAuth2Service.parseSignedRequest(signedRequest);
        if (data == null) {
            log.info("Failed OAuth2 deletion attempt");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request could not be parsed");
        }

        var userId = data.get("user_id").toString();
        var user = socialCredentialRepository
                .findBySocialIdAndRegistrationId(userId, "facebook")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist"))
                .getSocialUser();

        var username = user.getUserInfo().getUsername();
        userService.deleteUser(user);

        var token = oAuth2Service.getSignedRequest(Map.of("username", username));
        var link = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/delete-status")
                .queryParam("id", token)
                .toUriString();
        var jsonResponse = String.format("{url: \"%s\", confirmation_code: \"%s\"}", link, "delete200");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResponse);
    }

    @GetMapping("/delete-status")
    public ResponseEntity<String> getDeleteStatus(@RequestParam("id") String token) {
        String username = oAuth2Service.parseSignedRequest(token).get("username").toString();
        try {
            var user = userService.getUserByUsername(username);
        } catch (UserNotFoundException e) {
            return ResponseEntity.ok(String.format("User %s deleted from records", username));
        }

        return ResponseEntity
                .ok(String.format("Deletion failed for user %s. Please, contact me at dev.rodrigosp@gmail.com.",
                        username));
    }

}
