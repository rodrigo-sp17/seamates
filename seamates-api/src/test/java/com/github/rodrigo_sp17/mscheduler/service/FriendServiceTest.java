package com.github.rodrigo_sp17.mscheduler.service;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.friend.FriendService;
import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequestRepository;
import com.github.rodrigo_sp17.mscheduler.push.PushService;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class FriendServiceTest {

    private UserService userService = Mockito.mock(UserService.class);

    private FriendRequestRepository friendRequestRepository = Mockito
            .mock(FriendRequestRepository.class);

    private PushService pushService = Mockito.mock(PushService.class);

    private FriendService friendService = new FriendService(friendRequestRepository, pushService, userService);

    @Test
    public void testRequestFriendshipWhileAlreadyFriends() {
        AppUser user = TestData.getUsers().get(0);
        AppUser friend = TestData.getUsers().get(1);

        user.setFriends(new ArrayList<>());
        user.getFriends().add(friend);

        when(userService.getUserByUsername("john@doe123")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> friendService
                .requestFriendship("jane_girl18", "john@doe123"));
    }
}
