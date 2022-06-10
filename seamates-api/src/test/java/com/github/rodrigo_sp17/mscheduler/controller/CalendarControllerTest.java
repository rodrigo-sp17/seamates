package com.github.rodrigo_sp17.mscheduler.controller;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.calendar.CalendarController;
import com.github.rodrigo_sp17.mscheduler.calendar.CalendarService;
import com.github.rodrigo_sp17.mscheduler.security.UserDetailsServiceImpl;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CalendarController.class)
public class CalendarControllerTest extends AbstractControllerTest {

    @MockBean
    private CalendarService calendarService;

    @Test
    @WithMockUser("john@doe123")
    public void testGetAvailableFriends() throws Exception {
        LocalDate date = LocalDate.of(2021, 07, 01);
        List<AppUser> users = TestData.getUsers().subList(1, 3);

        when(calendarService.getAvailableFriends(eq(date), any()))
                .thenReturn(users);

        mvc.perform(get(new URI("/api/calendar/available"))
                .param("date", date.format(DateTimeFormatter.ISO_DATE)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Jane Doe")))
                .andExpect(content().string(containsString("Joao Silva")));
    }

}
