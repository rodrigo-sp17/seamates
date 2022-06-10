package com.github.rodrigo_sp17.mscheduler;

import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequest;
import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import com.github.rodrigo_sp17.mscheduler.shift.data.ShiftRequestDTO;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.data.UserInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

/**
 * Class containing common data used across unit and integration tests
 */
public class TestData {

    public static List<AppUser> getUsers() {
        AppUser user = new AppUser();
        UserInfo ui = new UserInfo();
        user.setUserInfo(ui);
        user.setUserId(1L);
        user.getUserInfo().setName("John Doe");
        user.getUserInfo().setEmail("john_doe@gmail.com");
        user.getUserInfo().setUsername("john@Doe123");
        user.getUserInfo().setPassword("testPassword");

        AppUser user2 = new AppUser();
        UserInfo ui2 = new UserInfo();
        user2.setUserInfo(ui2);
        user2.setUserId(2L);
        user2.getUserInfo().setName("Jane Doe");
        user2.getUserInfo().setEmail("jane_doe@gmail.com");
        user2.getUserInfo().setUsername("jane_girl18");
        user2.getUserInfo().setPassword("testPassword2");

        AppUser user3 = new AppUser();
        UserInfo ui3 = new UserInfo();
        user3.setUserInfo(ui3);
        user3.setUserId(3L);
        user3.getUserInfo().setName("Joao Silva");
        user3.getUserInfo().setEmail("joao_silva12@gmail.com");
        user3.getUserInfo().setUsername("joaozinn");
        user3.getUserInfo().setPassword("testPassword3");

        return Arrays.asList(user, user2, user3);
    }

    public static List<Shift> getShifts() {
        Shift shift1 = new Shift();
        shift1.setShiftId(1L);
        shift1.setUnavailabilityStartDate(LocalDate.of(2020, 11, 01));
        shift1.setBoardingDate(LocalDate.of(2020, 11, 02));
        shift1.setLeavingDate(LocalDate.of(2020, 11, 28));
        shift1.setUnavailabilityEndDate(LocalDate.of(2020, 11, 29));

        Shift shift2 = new Shift();
        shift2.setShiftId(2L);
        shift2.setUnavailabilityStartDate(LocalDate.of(2018, 12, 01));
        shift2.setBoardingDate(LocalDate.of(2018, 12, 03));
        shift2.setLeavingDate(LocalDate.of(2019, 01, 26));
        shift2.setUnavailabilityEndDate(LocalDate.of(2019, 01, 27));

        return Arrays.asList(shift1, shift2);
    }

    public static ShiftRequestDTO getShiftRequestDTO() {
        var shiftRequest = new ShiftRequestDTO();
        shiftRequest.setUnavailabilityStartDate(LocalDate.of(2020, 11, 01));
        shiftRequest.setBoardingDate(LocalDate.of(2020, 11, 02));
        shiftRequest.setLeavingDate(LocalDate.of(2020, 11, 28));
        shiftRequest.setUnavailabilityEndDate(LocalDate.of(2020, 11, 29));
        shiftRequest.setRepeat(1);
        return shiftRequest;
    }

    public static List<FriendRequest> getFriendRequest() {
        var fr = new FriendRequest();
        fr.setId(1L);
        fr.setSource(getUsers().get(0));
        fr.setTarget(getUsers().get(1));
        fr.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));

        var fr2 = new FriendRequest();
        fr2.setId(2L);
        fr2.setSource(getUsers().get(0));
        fr2.setTarget(getUsers().get(2));
        fr2.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));

        return Arrays.asList(fr, fr2);
    }




}
