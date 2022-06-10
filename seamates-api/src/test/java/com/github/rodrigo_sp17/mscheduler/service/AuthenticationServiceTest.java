package com.github.rodrigo_sp17.mscheduler.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationService;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private final String JWT_SECRET = "testsecret";
    private final Long JWT_EXPIRATION_MS = 864000000L;
    private final Long RESET_TOKEN_EXPIRATION_MS = 600000L;
    private final Long REFRESH_TOKEN_EXPIRATION_MS = 3024000000L;

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthenticationServiceTest(@Mock UserService userService,
                                     @Mock StringRedisTemplate redisTemplate) {
        this.userService = userService;
        this.authenticationService = new AuthenticationService(
                JWT_SECRET,
                JWT_EXPIRATION_MS,
                RESET_TOKEN_EXPIRATION_MS,
                REFRESH_TOKEN_EXPIRATION_MS,
                userService,
                redisTemplate
        );
    }

    @Test
    public void testEncodeDecodeRecoveryToken() {
        var user = TestData.getUsers().get(0);
        user.getUserInfo().setPassword("test");
        var expMinutes = Duration.ofMillis(RESET_TOKEN_EXPIRATION_MS).toMinutes();

        when(userService.getUserByUsername(any())).thenReturn(user);

        var resultToken = authenticationService.createRecoveryToken(user, LocalDateTime.now());
        assertNotNull(resultToken);

        var decodedToken = authenticationService
                .decodeRecoveryToken(user.getUserInfo().getUsername(), resultToken);

        assertEquals(user.getUserInfo().getUsername(), decodedToken.getSubject());

        // Ensures expiration is working
        assertThrows(JWTVerificationException.class, () -> authenticationService
                .decodeRecoveryToken(user.getUserInfo().getUsername(),
                        authenticationService.createRecoveryToken(user,
                                LocalDateTime.now().minusMinutes(expMinutes + 1))
                ));

        LocalDateTime timeOfJwt = LocalDateTime.of(2021, 02, 23,
                12, 10);

        // Tests determinism
        var firstToken = authenticationService.createRecoveryToken(user, timeOfJwt);
        var secondToken = authenticationService.createRecoveryToken(user, timeOfJwt);

        // Tests password dependency of JWT
        user.getUserInfo().setPassword("newpassword");
        var thirdToken = authenticationService.createRecoveryToken(user, timeOfJwt);

        assertEquals(firstToken, secondToken);
        assertNotEquals(firstToken, thirdToken);

    }
}
