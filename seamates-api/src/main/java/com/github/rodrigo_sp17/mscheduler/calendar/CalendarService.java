package com.github.rodrigo_sp17.mscheduler.calendar;

import com.github.rodrigo_sp17.mscheduler.friend.FriendService;
import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    @Autowired
    private final FriendService friendService;

    public CalendarService(FriendService friendService) {
        this.friendService = friendService;
    }

    public List<AppUser> getAvailableFriends(LocalDate date, String username) {
        List<AppUser> friends = friendService.getFriendsByUser(username);
        List<AppUser> availableFriends = new ArrayList<>();

        for (AppUser friend : friends) {
            List<Shift> busyShifts = friend.getShifts().stream()
                    .filter(s -> !(date.isBefore(s.getUnavailabilityStartDate()) ||
                            date.isAfter(s.getUnavailabilityEndDate())))
                    .collect(Collectors.toList());

            if (busyShifts.isEmpty()) {
                availableFriends.add(friend);
            }
        }
        return availableFriends;
    }
}
