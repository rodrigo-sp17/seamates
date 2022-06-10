package com.github.rodrigo_sp17.mscheduler.user;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationService;
import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredential;
import com.github.rodrigo_sp17.mscheduler.auth.data.SocialUserDTO;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.data.PasswordRequestDTO;
import com.github.rodrigo_sp17.mscheduler.user.data.UserDTO;
import com.github.rodrigo_sp17.mscheduler.user.data.UserInfo;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private AuthenticationService authenticationService;

    @Hidden
    @GetMapping
    public CollectionModel<String> getUsernames() {
        List<String> usernames = userService.getUsernames();
        Link selfLink = linkTo(methodOn(UserController.class).getUsernames()).withSelfRel();
        return CollectionModel.of(usernames).add(selfLink);
    }

    @Operation(summary = "Gets info from logged user", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirements
    @GetMapping("/me")
    public ResponseEntity<AppUser> getLoggedUser(Authentication auth) {
        String username = auth.getName();
        AppUser user = userService.getUserByUsername(username);
        user.add(linkTo(methodOn(UserController.class).editUserInfo(null)).withRel("edit"));
        user.add(linkTo(methodOn(UserController.class).getLoggedUser(auth)).withSelfRel());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Signs up a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User was created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody UserDTO req) {
        AppUser user = new AppUser();
        user.setUserInfo(new UserInfo());
        String errorMsg;

        // Name validation
        String name = req.getName().trim();
        user.getUserInfo().setName(name);

        // Usernames are case-insensitive and unique
        String username = req.getUsername()
                .trim()
                .toLowerCase();
        if (!userService.isUsernameAvailable(username)) {
            errorMsg = "The username already exists. Choose another one!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMsg);
        }
        user.getUserInfo().setUsername(username);

        // Email validation
        String email = req.getEmail().trim();    // Trimming to avoid common typos
        if (!userService.isEmailAvailable(email)) {
            errorMsg = "The email already exists. Choose another one!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMsg);
        }
        // TODO - send email confirmation
        user.getUserInfo().setEmail(email);

        // Password validation
        String password = req.getPassword();
        if (!password.equals(req.getConfirmPassword())) {
            errorMsg = "Confirmed password and password are not the same! " +
                    "Please, check your values and try again.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
        }
        user.getUserInfo().setPassword(passwordEncoder.encode(password));

        var addedUser = userService.saveUser(user);
        log.info("Created new user: " + addedUser.getUserInfo().getUsername());

        UserDTO request = getDTOFromUser(addedUser);
        Link toNewUser = linkTo(methodOn(UserController.class).getLoggedUser(null))
                .withSelfRel();
        return ResponseEntity.created(toNewUser.toUri()).body(request);
    }

    @Hidden
    @Operation(summary = "Signs up OAuth2 authenticated users", responses = {
            @ApiResponse(responseCode = "201", description = "User was created",
                    headers = @Header(name = "Authorization", description = "Bearer token")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/socialSignup")
    public ResponseEntity<UserDTO> socialSignup(@Valid @RequestBody SocialUserDTO req) {
        AppUser user = new AppUser();
        user.setUserInfo(new UserInfo());

        String username = req.getUsername()
                .trim()
                .toLowerCase();
        if (!userService.isUsernameAvailable(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The username already exists. Choose another one");
        }
        String name = req.getName().trim();
        String email = req.getEmail().trim();
        if (!userService.isEmailAvailable(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The email already exists. Choose another one");
        }

        user.getUserInfo().setName(name);
        user.getUserInfo().setEmail(email);
        user.getUserInfo().setUsername(username);

        var credential = new SocialCredential();
        credential.setSocialId(req.getSocialId());
        credential.setRegistrationId(req.getRegistrationId());
        credential.setSocialUser(user);

        user.setCredentials(List.of(credential));

        var addedUser = userService.saveUser(user);

        String addedUsername = addedUser.getUserInfo().getUsername();
        log.info("Created new user: " + addedUsername);
        String token = authenticationService.login(addedUsername);

        UserDTO request = getDTOFromUser(addedUser);
        Link toNewUser = linkTo(methodOn(UserController.class).getLoggedUser(null))
                .withSelfRel();

        return ResponseEntity.created(toNewUser.toUri())
                .header("Authorization", "Bearer " + token)
                .body(request);
    }


    @Operation(summary = "Edits info for an existing user", responses = {
            @ApiResponse(responseCode = "200", description = "Edition successful"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @SecurityRequirements
    @PutMapping
    public ResponseEntity<UserDTO> editUserInfo(@RequestBody UserDTO req) {
        AppUser userToEdit = userService.getUserById(req.getUserId());
        if (req.getName() != null) {
            userToEdit.getUserInfo().setName(req.getName());
        }
        if (req.getEmail() != null) {
            if (!userService.isEmailAvailable(req.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "The email already exists. Choose another one");
            }
            userToEdit.getUserInfo().setEmail(req.getEmail());
        }
        AppUser editedUser = userService.saveUser(userToEdit);
        UserDTO request = getDTOFromUser(editedUser);
        request.add(linkTo(methodOn(UserController.class).getLoggedUser(null)).withSelfRel());
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Permanently deletes an AppUser", responses = {
            @ApiResponse(responseCode = "204", description = "Deletion successful"),
            @ApiResponse(responseCode = "403", description = "Account deletion was not authorized"),
            @ApiResponse(responseCode = "404", description = "User was not found"),
            @ApiResponse(responseCode = "500", description = "Unknown server error")
    })
    @SecurityRequirements
    @DeleteMapping("/delete")
    public ResponseEntity<UserDTO> deleteUser(Authentication auth,
                                                        @RequestHeader String password) {
        var userToDelete = userService.getUserByUsername(auth.getName());
        var storedPassword = userToDelete.getUserInfo().getPassword();

        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account deletion unauthorized");
        }

        if (userService.deleteUser(userToDelete)) {
            log.info("Deleted user: " + userToDelete.getUserInfo().getUsername());
            return ResponseEntity.noContent().build();
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Deletion failed");
    }

    @Operation(summary = "Starts account recovery process for a user", responses = {
            @ApiResponse(responseCode = "200", description = "Recovery initiated")
    })
    @PostMapping("/recover")
    @ResponseStatus(HttpStatus.OK)
    public void recoverAccount(@RequestParam String user) {
        AppUser appUser = null;

        // Attempts to fetch user
        try {
            appUser = userService.getUserByUsername(user);
        } catch (UserNotFoundException e) {
            log.info("Failed recovery attempt for user: " + user);
        }

        if (appUser != null) {
            String resetToken = authenticationService.createRecoveryToken(appUser,
                    LocalDateTime.now());

            // Send email
            var email = createRecoveryEmail(resetToken, appUser);
            javaMailSender.send(email);
            log.info("Recovery e-mail sent to: " + appUser.getUserInfo().getEmail());
        }
    }

    @Hidden
    @PostMapping("/refresh")
    public ResponseEntity<String> changePassword(@RequestHeader String authorization) {
        // validates
        // returns change password window
        // TODO - couple with React page. Unused for API password reset.
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resets the password for a given recovery token", responses = {
            @ApiResponse(responseCode = "200", description = "Reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid data or non-matching passwords"),
            @ApiResponse(responseCode = "403", description = "Reset not authorized")
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestHeader("reset") String token,
                                                @Valid @RequestBody PasswordRequestDTO passwordRequest) {
        String errorMsg;

        // Verifies token
        DecodedJWT decodedToken;
        try {
            decodedToken = authenticationService
                    .decodeRecoveryToken(passwordRequest.getUsername(), token);
        } catch (JWTVerificationException e) {
            errorMsg = "You are not authorized to perform resets";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMsg);
        }

        // Verifies passwords
        if (!passwordRequest.getPassword().equals(passwordRequest.getConfirmPassword())) {
            errorMsg = "Passwords do not match";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
        }

        // Edits the password for user
        var user = userService.getUserByUsername(decodedToken.getSubject());
        user.getUserInfo().setPassword(passwordEncoder
                .encode(passwordRequest.getPassword()));

        userService.saveUser(user);

        return ResponseEntity.ok().build();
    }


    // Helper methods
    private UserDTO getDTOFromUser(AppUser user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUserInfo().getUsername());
        dto.setName(user.getUserInfo().getName());
        dto.setEmail(user.getUserInfo().getEmail());
        return dto;
    }

    private SimpleMailMessage createRecoveryEmail(String resetToken, AppUser user) {
        String message = String.format(
                "Olá, %s! %n%n" +
                        "Recebemos uma solicitação de recuperação de conta em seu nome. " +
                        "Caso realmente o tenha feito, siga o link fornecido abaixo para trocar " +
                        "sua senha: %n%n" +
                        "%s %n%n" +
                        "Atenciosamente, %n%n" +
                        "Equipe Agenda Marítima",
                user.getUserInfo().getName(),
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/changePassword")
                        .queryParam("reset", resetToken)
                        .toUriString()
        );

        var email = new SimpleMailMessage();
        email.setSubject("Agenda Marítima - Recuperação de Conta");
        email.setText(message);
        email.setTo(user.getUserInfo().getEmail());

        return email;
    }

}
