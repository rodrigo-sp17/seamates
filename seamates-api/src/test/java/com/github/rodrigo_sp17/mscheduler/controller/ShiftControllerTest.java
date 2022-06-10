package com.github.rodrigo_sp17.mscheduler.controller;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.shift.ShiftController;
import com.github.rodrigo_sp17.mscheduler.shift.ShiftService;
import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import com.github.rodrigo_sp17.mscheduler.shift.data.ShiftRequestDTO;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ShiftController.class)
@ExtendWith(MockitoExtension.class) // This annotation enables mockito on JUnit5
@AutoConfigureJsonTesters
public class ShiftControllerTest extends AbstractControllerTest {

    @MockBean
    private ShiftService shiftService;

    @MockBean
    private UserService userService;

    @Autowired
    private JacksonTester<ShiftRequestDTO> shiftRequestJson;

    @Captor
    ArgumentCaptor<List<Shift>> acList;

    @Captor
    ArgumentCaptor<Shift> acShift;

    @Captor
    ArgumentCaptor<Long> acLong;


    @Test
    @WithMockUser(username = "john@doe123")
    public void testGetShiftById() throws Exception {
        var shift = TestData.getShifts().get(0);
        shift.setOwner(TestData.getUsers().get(0));

        when(shiftService.getShiftById(eq(1L), eq("john@doe123")))
                .thenReturn(shift);

        mvc.perform(get(new URI("/api/shift/1")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2020")));

        verify(shiftService, atLeast(1)).getShiftById(any(),any());
    }

    @Test
    public void testGetShifts() {
        // TODO
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testAddShifts() throws Exception {
        var req = TestData.getShiftRequestDTO();
        when(shiftService.addShifts(any(), eq("john@doe123"))).thenReturn(TestData.getShifts());

        mvc.perform(post(new URI("/api/shift/add"))
                .content(shiftRequestJson.write(req).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(shiftService).addShifts(acList.capture(), any());
        var result = acList.getValue();
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2021, 01, 19),
                result.get(1).getLeavingDate());
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testEditShift() throws Exception {
        var req = TestData.getShiftRequestDTO();
        req.setShiftId(null);

        var shift = TestData.getShifts().get(0);
        shift.setOwner(TestData.getUsers().get(0));

        when(shiftService.getShiftById(eq(1L), eq("john@doe123"))).thenReturn(shift);

        mvc.perform(put(new URI("/api/shift/edit"))
                .content(shiftRequestJson.write(req).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        req.setShiftId(1L);
        req.setUnavailabilityStartDate(req.getUnavailabilityStartDate().plusDays(3));

        mvc.perform(put(new URI("/api/shift/edit"))
                .content(shiftRequestJson.write(req).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        req.setUnavailabilityStartDate(req.getUnavailabilityStartDate().minusDays(3));
        LocalDate editedDate = LocalDate.of(2020, 11, 03);
        req.setBoardingDate(editedDate);
        shift.setBoardingDate(editedDate);

        when(shiftService.editShift(any())).thenReturn(shift);

        mvc.perform(put(new URI("/api/shift/edit"))
                .content(shiftRequestJson.write(req).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2020-11-03")));

        verify(shiftService).editShift(acShift.capture());
        Shift captured = acShift.getValue();
        assertEquals(editedDate, captured.getBoardingDate());
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testRemoveShift() throws Exception {
        var shift = TestData.getShifts().get(0);

        when(shiftService.getShiftById(eq(2L), eq("john@doe123"))).thenReturn(null);
        when(shiftService.getShiftById(eq(1L), eq("john@doe123"))).thenReturn(shift);

        mvc.perform(delete(new URI("/api/shift/remove"))
                .param("id", "2"))
                .andExpect(status().isBadRequest());

        mvc.perform(delete(new URI("/api/shift/remove"))
                .param("id", "1"))
                .andExpect(status().isNoContent());

        verify(shiftService).removeShift(acLong.capture());
        Long idToDelete = acLong.getValue();

        assertEquals(1, idToDelete);
    }


}
