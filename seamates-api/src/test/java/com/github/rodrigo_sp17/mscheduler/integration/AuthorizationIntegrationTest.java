package com.github.rodrigo_sp17.mscheduler.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void test_SignupAndAuthorization() throws Exception {
        mvc.perform(get(new URI("/api/user")))
                .andExpect(status().isForbidden());

        mvc.perform(get(new URI("/api/shifts")))
                .andExpect(status().isForbidden());

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("name", "Maria Silva");
        jsonRequest.put("username", "Maria_silva12");
        jsonRequest.put("password", "pasWdssh@#!123");
        jsonRequest.put("confirmPassword", "pasWdssh@#!123");
        jsonRequest.put("email", "maria_silva@email.com");

        mvc.perform(post(new URI("/api/user/signup"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Maria")))
                .andExpect(content().string(containsString("maria_silva12")))
                .andExpect(content().string(containsString("maria_silva@email.com")))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("confirmPassword"))));

        // Attempts wrong login
        var jsonLogin = new JSONObject();
        jsonLogin.put("username", "maria_silva12")
                .put("password", "wrong password");

        mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isUnauthorized());

        // Rightful login
        jsonLogin.put("password", "pasWdssh@#!123");
        var result = mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk())
                .andReturn();

        String token = result
                .getResponse()
                .getHeader("Authorization");

        mvc.perform(get(new URI("/api/user"))
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emailLogin() throws Exception {
        JSONObject jsonRequest = new JSONObject();
        var username = "sumother12";
        var password = "password123";
        var email = "someother@gmail.com";
        jsonRequest.put("name", "Some Other");
        jsonRequest.put("username", username);
        jsonRequest.put("password", password);
        jsonRequest.put("confirmPassword", password);
        jsonRequest.put("email", email);

        mvc.perform(post(new URI("/api/user/signup"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest.toString()))
                .andExpect(status().isCreated());

        var jsonLogin = new JSONObject();
        jsonLogin.put("username", username)
                .put("password", password);

        mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk());

        jsonLogin.put("username", email);
        mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isOk());

        jsonLogin.put("password", "wrongpass");
        mvc.perform(post(new URI("/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void test_noDuplicateEmailOnSignup() throws Exception {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("name", "Lazio Lazia");
        jsonRequest.put("username", "laazio123");
        jsonRequest.put("password", "pasWdssh@#!123");
        jsonRequest.put("confirmPassword", "pasWdssh@#!123");
        jsonRequest.put("email", "fifthwheel@gmail.com");

        mvc.perform(post(new URI("/api/user/signup"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest.toString()))
                .andExpect(status().isConflict());

        jsonRequest.put("email", "someuniquemail@gmail.com");
        mvc.perform(post(new URI("/api/user/signup"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest.toString()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("Edited Guy")
    public void test_noDuplicateEmailOnEdition() throws Exception {
        var request = new JSONObject();
        request.put("userId", 11);
        request.put("email", "thirdwheel@gmail.com");

        mvc.perform(put(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()))
                .andExpect(status().isConflict());

        request.put("email", "definetelynew@gmail.com");

        mvc.perform(put(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()))
                .andExpect(status().isOk());
    }
}
