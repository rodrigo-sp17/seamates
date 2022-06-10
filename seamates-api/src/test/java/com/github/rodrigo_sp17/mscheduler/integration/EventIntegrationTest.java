package com.github.rodrigo_sp17.mscheduler.integration;

import com.github.rodrigo_sp17.mscheduler.event.data.EventDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class EventIntegrationTest {

    private final String URI = "/api/event";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JacksonTester<EventDTO> dtoJson;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithMockUser("third_wheel")
    public void test_getEvents() throws Exception {
        // Get all
        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Third Event")))
                .andExpect(content().string(containsString("Fifth Event")));

        // Get owned
        mvc.perform(get(URI + "/owned"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Third Event")))
                .andExpect(content().string(not(containsString("Fifth Event"))));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_createEvent() throws Exception {
        // Assert no events
        mvc.perform(get(new URI(URI)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Fourth Event"))));

        // Wrong title and past start date
        var json = new JSONObject();
        json.put("title", "");
        json.put("start", "2020-12-01T12:00");

        mvc.perform(post(new URI(URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("title")))
                .andExpect(content().string(containsString("start")));

        // Right request, expected all day = true
        json.put("title", "Fourth Event");
        json.put("start", LocalDateTime.now().plusMinutes(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        var arr = new JSONArray();
        arr.put("fourth_wheel");
        json.put("invitedUsernames", arr);

        mvc.perform(post(new URI(URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Fourth Event")))
                .andExpect(content().string(not(containsString("fourth_wheel"))))
                .andExpect(content().string(containsString("\"allDay\":true")));

        // Ensures visibility of new event
        mvc.perform(get(URI + "/owned"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fourth Event")));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_confirmEvent() throws Exception {
        var result = mvc.perform(get(URI + "/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var dto = dtoJson.parseObject(result);
        assertTrue(dto.getInvitedUsernames().contains("third_wheel"));
        assertFalse(dto.getConfirmedUsernames().contains("third_wheel"));

        mvc.perform(put(new URI(URI + "/confirm/2")))
                .andExpect(status().isOk());

        // Checks again
        var newResult = mvc.perform(get(URI + "/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();


        var newDto = dtoJson.parseObject(newResult);
        assertFalse(newDto.getInvitedUsernames().contains("third_wheel"));
        assertTrue(newDto.getConfirmedUsernames().contains("third_wheel"));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_inviteToEvent() throws Exception {
        // Ensures it is accessible
        mvc.perform(get(URI + "/15"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("fifth_wheel"))));

        var json = new JSONObject();
        // Ensures id is ignored - path matters
        json.put("eventId", 72);
        json.put("invitedUsername", "fifth_wheel");
        // Ensures owner can't change status
        json.put("isConfirmed", true);

        mvc.perform(post(new URI(URI + "/15" + "/invite" + "/fifth_wheel"))
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("fifth_wheel")))
                .andExpect(content().string(not(containsString("72"))))
                .andExpect(content().string(containsString("false")));

        mvc.perform(get(URI + "/15"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("fifth_wheel")));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_uninvite() throws Exception {
        // Will remove fifth wheel (8) from second event (3)
        mvc.perform(get(URI + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("fifth_wheel")));

        mvc.perform(delete(new URI(URI + "/3" + "/invite" + "/fifth_wheel")))
                .andExpect(status().isNoContent());

        mvc.perform(get(URI + "/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("fifth_wheel"))));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_editEventDetails() throws Exception {
        // Attempts to edit Third Event
        var json = new JSONObject();
        json.put("id", 5);
        json.put("title", "New Zero Title");
        json.put("start", "2035-12-01T12:00");
        json.put("allDay", true);
        var arr = new JSONArray();
        arr.put("fifth_wheel");
        json.put("invitedUsernames", arr);

        mvc.perform(patch(new URI(URI + "/15"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Zero Title")))
                .andExpect(content().string(containsString("2035-12-01")))
                .andExpect(content().string(containsString("\"allDay\":true")))
                .andExpect(content().string(not(containsString("fifth_wheel"))));

        // Ensures it is accessible
        mvc.perform(get(URI + "/owned"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Zero Title")))
                .andExpect(content().string(containsString("2035-12-01")))
                .andExpect(content().string(containsString("\"allDay\":true")))
                .andExpect(content().string(not(containsString("fifth_wheel"))));
    }

    @Test
    @WithMockUser("third_wheel")
    public void test_deleteEvent() throws Exception {
        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("First Event")));

        mvc.perform(delete(new URI(URI + "/4")))
                .andExpect(status().isNoContent());

        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("First Event"))));
    }

    @Test
    @WithMockUser("fourth_wheel")
    public void test_noUnauthorizedAccess() throws Exception {
        // Can't access another people's events
        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Third Event"))))
                .andExpect(content().string(not(containsString("Fifth Event"))));

        // Can't edit another people's events
        var json = new JSONObject();
        json.put("id", 1);
        json.put("title", "4th changed another one`s title");
        mvc.perform(patch(new URI(URI + "/15"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isNotFound());

        // Can`t delete another people's events
        mvc.perform(delete(new URI(URI + "/15")))
                .andExpect(status().isNotFound());
    }
}
