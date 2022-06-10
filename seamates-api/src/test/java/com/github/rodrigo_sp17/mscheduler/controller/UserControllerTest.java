package com.github.rodrigo_sp17.mscheduler.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.user.UserController;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(value = UserController.class)
@AutoConfigureJsonTesters
public class UserControllerTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreateUser() throws Exception {
        AppUser parsedUser = TestData.getUsers().get(0);
        parsedUser.setUserId(null);

        when(userService.saveUser(any())).thenReturn(TestData.getUsers().get(0));
        when(userService.isUsernameAvailable("john@doe123")).thenReturn(true);
        when(userService.isEmailAvailable(parsedUser.getUserInfo().getEmail())).thenReturn(true);

        // Manually parses the JSON to allow @JsonIgnore annotation on response
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("username", parsedUser.getUserInfo().getUsername());
        jsonRequest.put("name", parsedUser.getUserInfo().getName());
        jsonRequest.put("password", parsedUser.getUserInfo().getPassword());
        jsonRequest.put("confirmPassword", parsedUser.getUserInfo().getPassword());
        jsonRequest.put("email", parsedUser.getUserInfo().getEmail());

        var mvcResult = mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = mvcResult.getResponse().getContentAsString();

        assertTrue(responseJson.contains(parsedUser.getUserInfo().getName()));
        assertTrue(responseJson.contains(parsedUser.getUserInfo().getUsername()));
        assertTrue(responseJson.contains(parsedUser.getUserInfo().getEmail()));
        // Ensures password is not returned
        assertFalse(responseJson.contains(parsedUser.getUserInfo().getPassword()));
    }

    @Test
    public void testSignupExoticNames() throws Exception {
        when(userService.getUserByUsername("someusername")).thenReturn(TestData.getUsers().get(0));
        when(userService.isUsernameAvailable(any())).thenReturn(true);
        when(userService.isEmailAvailable(any())).thenReturn(true);
        when(userService.saveUser(any())).thenReturn(TestData.getUsers().get(0));

        JSONObject json = new JSONObject();
        json.put("username", "someusername");
        json.put("password", "anypasssword");
        json.put("confirmPassword", "anypasssword");
        json.put("email", "some@email.com");

        json.put("name", "João Silva");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        json.put("name", "Héllen Marquês");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        json.put("name", "Joseph Heckt-Philson");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        json.put("name", "Clayton Türunen");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        json.put("name", "Name with 1");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("numbers")));

        json.put("name", "Name /");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("must")));
    }

    @Test
    public void testWrongSignup() throws Exception {
        AppUser parsedUser = TestData.getUsers().get(0);
        parsedUser.setUserId(null);
        parsedUser.getUserInfo().setUsername("john@doe123");

        when(userService.getUserByUsername("john@doe123")).thenReturn(TestData.getUsers().get(0));
        when(userService.saveUser(parsedUser)).thenReturn(TestData.getUsers().get(0));
        when(userService.isUsernameAvailable(any())).thenReturn(false);

        // Test wrong name
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("name", "");
        jsonRequest.put("username", parsedUser.getUserInfo().getUsername());
        jsonRequest.put("password", parsedUser.getUserInfo().getPassword());
        jsonRequest.put("confirmPassword", parsedUser.getUserInfo().getPassword());
        jsonRequest.put("email", parsedUser.getUserInfo().getEmail());

        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Names")));

        jsonRequest.put("name", "Rodrigo1 Rodrigues");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Names must")));

        jsonRequest.put("name", "John Doe");

        // Test wrong username
        // Test too short
        jsonRequest.put("username", "john1");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Usernames must be")));

        // Test case-insensitive and wrong space
        when(userService.isUsernameAvailable(eq("john@doe123"))).thenReturn(false);
        jsonRequest.put("username", "john@DOE123 ");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("username already")));

        jsonRequest.put("username", "newUser");

        when(userService.isUsernameAvailable(any())).thenReturn(true);
        when(userService.isEmailAvailable(any())).thenReturn(true);
        // Test wrong password
        jsonRequest.put("confirmPassword", "testPassword1");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Confirmed password")));

        // Test too short
        jsonRequest.put("password", "short");
        jsonRequest.put("confirmPassword", "short");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Passwords must ")));

        jsonRequest.put("password", "short1234");
        jsonRequest.put("confirmPassword", "short1234");

        // Test wrong email
        jsonRequest.put("email", "notanemail@");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email")));
        jsonRequest.put("email", "");
        mvc.perform(post(new URI("/api/user/signup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("mandatory")));
    }

    @Test
    public void testSocialSignupValidation() throws Exception {
        when(userService.getUserByUsername("john@doe123")).thenReturn(TestData.getUsers().get(0));
        when(userService.saveUser(any())).thenReturn(TestData.getUsers().get(0));
        when(userService.isUsernameAvailable(any())).thenReturn(false);
        when(userService.isEmailAvailable(any())).thenReturn(false);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("name", "");
        jsonRequest.put("username", "john@doe123");
        jsonRequest.put("password", "any password");
        jsonRequest.put("confirmPassword", "not matching");
        jsonRequest.put("email", "notemail");

        mvc.perform(post(new URI("/api/user/socialSignup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Names")))
                .andExpect(content().string(not(containsString("username"))))
                .andExpect(content().string(containsString("email")));

        jsonRequest.put("name", "John Doe");
        jsonRequest.put("email", "valid@email.com");

        mvc.perform(post(new URI("/api/user/socialSignup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("social")));

        jsonRequest.put("socialId", "anysocial");
        jsonRequest.put("registrationId", "regid");

        mvc.perform(post(new URI("/api/user/socialSignup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("username")));

        when(userService.isUsernameAvailable(any())).thenReturn(true);
        when(userService.isEmailAvailable(any())).thenReturn(true);
        mvc.perform(post(new URI("/api/user/socialSignup"))
                .content(jsonRequest.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testEditUser() throws Exception {
        AppUser originalUser = TestData.getUsers().get(0);
        AppUser editedUser = TestData.getUsers().get(0);
        editedUser.getUserInfo().setEmail("newmail@hotmail.com");
        var request = new JSONObject();
        request.put("userId", originalUser.getUserId());
        request.put("email", "newmail@hotmail.com");

        when(userService.getUserById(1L)).thenReturn(originalUser);
        when(userService.saveUser(any())).thenReturn(editedUser);
        when(userService.isEmailAvailable(any())).thenReturn(true);
        mvc.perform(put(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("newmail@hotmail.com")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testDeleteUser() throws Exception {
        AppUser user = TestData.getUsers().get(0);

        when(userService.getUserByUsername(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("testPassword"), eq("testPassword")))
                .thenReturn(true);
        when(userService.deleteUser(any())).thenReturn(true);

        mvc.perform(delete(new URI("/api/user/delete"))
                .header("password", "testPassword1"))
                .andExpect(status().isForbidden());

        // Testing right password
        mvc.perform(delete(new URI("/api/user/delete"))
                .header("password", "testPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRecoverAccount() throws Exception {
        AppUser user = TestData.getUsers().get(0);
        when(userService.getUserByUsername(eq(user.getUserInfo().getUsername()))).thenReturn(user);
        when(userService.getUserByUsername(eq(user.getUserInfo().getEmail())))
                .thenThrow(new UserNotFoundException());
        when(userService.getUserByEmail(eq(user.getUserInfo().getEmail()))).thenReturn(user);
        when(userService.getUserByEmail(eq("ababwa")))
                .thenThrow(new UserNotFoundException());

        mvc.perform(post(new URI("/api/user/recover"))
                .param("user", "ababwa"))
                .andExpect(status().isOk());

        mvc.perform(post(new URI("/api/user/recover"))
                .param("user", user.getUserInfo().getUsername()))
                .andExpect(status().isOk());

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testResetPassword() throws Exception {
        AppUser user = TestData.getUsers().get(0);
        String token = JWT.create()
                .withSubject(user.getUserInfo().getUsername())
                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)))
                .sign(Algorithm.HMAC512("secret"));

        DecodedJWT decoded = JWT.decode(token);

        when(authenticationService.decodeRecoveryToken(any(), eq(token)))
                .thenThrow(JWTVerificationException.class);
        when(authenticationService.decodeRecoveryToken(eq(user.getUserInfo().getUsername()), eq(token)))
                .thenReturn(decoded);
        when(authenticationService.decodeRecoveryToken(any(), eq("random")))
                .thenThrow(JWTVerificationException.class);

        when(userService.getUserByUsername(user.getUserInfo().getUsername())).thenReturn(user);

        JSONObject json = new JSONObject();
        json.put("username", user.getUserInfo().getUsername());
        json.put("password", "newPassword");
        json.put("confirmPassword", "newPassword");

        mvc.perform(post(new URI("/api/user/resetPassword"))
                .header("reset", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isOk());
        verify(userService, times(1)).saveUser(any());

        mvc.perform(post(new URI("/api/user/resetPassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).saveUser(any());

        mvc.perform(post(new URI("/api/user/resetPassword"))
                .header("reset", "random")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isForbidden());
        verify(userService, times(1)).saveUser(any());

        json.put("username", "wrongUsername");
        mvc.perform(post(new URI("/api/user/resetPassword"))
                .header("reset", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isForbidden());
        verify(userService, times(1)).saveUser(any());

        json.put("username", user.getUserInfo().getUsername());
        json.put("confirmPassword", "wrong");
        mvc.perform(post(new URI("/api/user/resetPassword"))
                .header("reset", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest());

        json.put("password", "small");
        json.put("confirmPassword", "small");
        mvc.perform(post(new URI("/api/user/resetPassword"))
                .header("reset", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Passwords")));
        verify(userService, times(1)).saveUser(any());
    }


}
