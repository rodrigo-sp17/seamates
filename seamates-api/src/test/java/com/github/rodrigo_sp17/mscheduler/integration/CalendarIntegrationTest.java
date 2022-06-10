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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithMockUser("jane_girl18")
    public void test_ShowAvailableUsers() throws Exception {
        // Tested friend is joaozinn
        var friendUsername = "joaozinn";
        var friendUnavailableSince = "2027-04-14";
        var friendAvailableSince = "2027-04-19";

        // Request date inside range
        mvc.perform(get(new URI("/api/calendar/available"))
                .param("date", "2027-04-18"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(friendUsername))));

        // Request date matching the edge dates
        mvc.perform(get(new URI("/api/calendar/available"))
                .param("date", friendAvailableSince))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(friendUsername))));

        mvc.perform(get(new URI("/api/calendar/available"))
                .param("date", friendUnavailableSince))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(friendUsername))));

        // Request date matching outside range
        mvc.perform(get(new URI("/api/calendar/available"))
                .param("date", "2027-04-13"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(friendUsername)));
    }

}
