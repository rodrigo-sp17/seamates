package com.github.rodrigo_sp17.mscheduler.integration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"jwt.expiration-ms=-5000"})
@AutoConfigureMockMvc
public class RefreshIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void test_refreshToken() throws Exception {
        // logins
        var jsonLogin = new JSONObject();
        jsonLogin.put("username", "real_guy")
                .put("password", "testpassword");

        var bearerToken = mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Authorization");

        // expected expired token
        mvc.perform(get(new URI("/api/user"))
                .header("Authorization", bearerToken))
                .andExpect(status().isForbidden());

        // attempts to refresh
        var newBearerToken = mvc.perform(post(new URI("/refresh"))
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn().getResponse().getHeader("Authorization");

        assertNotNull(newBearerToken);
        assertFalse(newBearerToken.isBlank());
    }
}
