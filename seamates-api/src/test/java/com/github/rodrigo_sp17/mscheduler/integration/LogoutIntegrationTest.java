package com.github.rodrigo_sp17.mscheduler.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void test_logout() throws Exception {
        // logins
        var jsonLogin = new JSONObject();
        jsonLogin.put("username", "real_guy")
                .put("password", "testpassword");

        var bearerToken = mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Authorization");

        // checks ok status
        mvc.perform(get(new URI("/api/user"))
                .header("Authorization", bearerToken))
                .andExpect(status().isOk());

        // attempts to refresh
        var newBearerToken = mvc.perform(post(new URI("/refresh"))
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn().getResponse().getHeader("Authorization");

        // logout
        mvc.perform(post(new URI("/logout"))
                .header("Authorization", newBearerToken))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Authorization"));

        // attempts to refresh again
        mvc.perform(post(new URI("/refresh"))
                .header("Authorization", newBearerToken))
                .andExpect(status().isForbidden());

        // re-logins
        bearerToken = mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Authorization");

        // re-refresh
        newBearerToken = mvc.perform(post(new URI("/refresh"))
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn().getResponse().getHeader("Authorization");

        // re-access
        mvc.perform(get(new URI("/api/user"))
                .header("Authorization", newBearerToken))
                .andExpect(status().isOk());
    }
}
