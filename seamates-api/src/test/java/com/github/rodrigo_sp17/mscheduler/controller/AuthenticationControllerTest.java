package com.github.rodrigo_sp17.mscheduler.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.net.URI;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest extends AbstractControllerTest {

    @Test
    public void test_correctTokenSent() throws Exception {
        when(authenticationService.refreshToken(eq("exampletoken")))
                .thenReturn(Optional.of("refreshedtoken"));
        when(authenticationService.verifyJWTToken(eq("exampletoken")))
                .thenReturn(JWT.decode(JWT.create().withSubject("any").sign(Algorithm.HMAC512("secret"))));

        mvc.perform(post(new URI("/refresh"))
                .header("Authorization", "Bearer exampletoken"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer refreshedtoken"));
    }

    @Test
    public void test_noHeader() throws Exception {
        mvc.perform(post(new URI("/refresh")))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    public void test_noTokenSent() throws Exception {
        mvc.perform(post(new URI("/refresh"))
                .header("Authorization", ""))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"));
    }
}
