package com.github.rodrigo_sp17.mscheduler.integration;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.shift.data.ShiftRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
public class ShiftIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private JacksonTester<ShiftRequestDTO> shiftRequestJson;

    @Test
    @WithMockUser("jane_girl18")
    public void test_AddShifts() throws Exception {
        var request = TestData.getShiftRequestDTO();

        mvc.perform(get("/api/shift"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("2020"))));

        mvc.perform(get("/api/shift/1"))
                .andExpect(status().isNotFound());

        mvc.perform(post(new URI("/api/shift/add"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(shiftRequestJson.write(request).getJson()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2020-11-01")))
                .andExpect(content().string(containsString("2021-01-19")));

        mvc.perform(get("/api/shift"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2020-11-01")))
                .andExpect(content().string(containsString("2021-01-19")));
    }

    @Test
    @WithMockUser("jane_girl18")
    public void test_RemoveShifts() throws Exception {
        mvc.perform(get("/api/shift"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2025-04-13")))
                .andExpect(content().string(containsString("2025-04-15")))
                .andExpect(content().string(containsString("2025-05-17")))
                .andExpect(content().string(containsString("2025-05-18")));

        mvc.perform(delete(new URI("/api/shift/remove"))
                .param("id", "330"))
                .andExpect(status().isNotFound());

        mvc.perform(delete(new URI("/api/shift/remove"))
                .param("id", "11"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/shift"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("2025-04-13"))))
                .andExpect(content().string(not(containsString("2025-04-15"))))
                .andExpect(content().string(not(containsString("2025-05-17"))))
                .andExpect(content().string(not(containsString("2025-05-18"))));
    }

}
