package com.github.rodrigo_sp17.mscheduler.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.Year;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FriendIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithMockUser("jane_girl18")
    public void test_FriendsVisible() throws Exception {
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Joao Silva")))
                .andExpect(content().string(containsString("joaozinn")));

        mvc.perform(get(new URI("/api/friend/request")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Joao Silva"))));
    }

    @Test
    @WithMockUser("jane_girl18")
    public void test_RequestFriendship() throws Exception {
        var myUsername = "jane_girl18";
        var myName = "Jane Doe";
        var myEmail = "jane_doe@gmail.com";
        var username = "fulaninn";
        var name = "Fulano Fulanaldo";

        // Ensures no friendship or request
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(username))))
                .andExpect(content().string(not(containsString(name))));

        mvc.perform(get(new URI("/api/friend/request")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(name))));

        // Attempts wrong request
        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", "inexistantuser"))
                .andExpect(status().isNotFound());

        // Performs right request
        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", username))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(name)))
                .andExpect(content().string(containsString(myName)))
                .andExpect(content().string(containsString(Year.now().toString())));

        // Ensures it is not a friend yet, but a request is visible
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(username))))
                .andExpect(content().string(not(containsString(name))));

        mvc.perform(get(new URI("/api/friend/request")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(name)));

        // Checks if a new attempt returns the same request
        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", username))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(name)))
                .andExpect(content().string(containsString(myName)))
                .andExpect(content().string(containsString(Year.now().toString())));

        // Checks request for another user and accepts
        mvc.perform(get(new URI("/api/friend"))
                .with(user(username).password("").roles("")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(myName))));

        mvc.perform(get(new URI("/api/friend/request"))
                .with(user(username).password("").roles("")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(myName)));

        mvc.perform(post(new URI("/api/friend/accept"))
                .param("username", myUsername)
                .with(user(username).password("").roles("")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(myName)))
                .andExpect(content().string(containsString(myUsername)))
                .andExpect(content().string(containsString(myEmail)));

        mvc.perform(get(new URI("/api/friend"))
                .with(user(username).password("").roles("")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(myName)));

        mvc.perform(get(new URI("/api/friend/request"))
                .with(user(username).password("").roles("")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(myName))));

        // Ensures visibility for original user
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(username)))
                .andExpect(content().string(containsString(name)));

        mvc.perform(get(new URI("/api/friend/request")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(name))));
    }

    @Test
    @WithMockUser("jane_girl18")
    public void test_Unfriend() throws Exception {
        // Cant unfriend strangers
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("third_wheel"))));

        mvc.perform(delete(new URI("/api/friend/remove"))
                .param("username", "third_wheel"))
                .andExpect(status().isNotFound());

        // Performs deletion and checks result
        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hated13")));

        mvc.perform(delete(new URI("/api/friend/remove"))
                .param("username", "hated13"))
                .andExpect(status().isNoContent());

        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("hated13"))));
    }


}
