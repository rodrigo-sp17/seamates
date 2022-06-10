package com.github.rodrigo_sp17.mscheduler.controller;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.friend.FriendController;
import com.github.rodrigo_sp17.mscheduler.friend.FriendService;
import com.github.rodrigo_sp17.mscheduler.security.UserDetailsServiceImpl;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.data.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendController.class)
public class FriendControllerTest extends AbstractControllerTest {

    @MockBean
    private FriendService friendService;

    @Test
    @WithMockUser(username = "john@doe123")
    public void testGetFriends() throws Exception {
        var users = getUsers();

        when(friendService.getFriendsByUser(eq("john@doe123")))
                .thenReturn(users);

        mvc.perform(get(new URI("/api/friend")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString("Jane Doe")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testGetFriendRequests() throws Exception {
        var fr = TestData.getFriendRequest();
        var users = getUsers();


        when(friendService.getFriendRequestsForUser(eq("john@doe123")))
                .thenReturn(fr);

        mvc.perform(get(new URI("/api/friend/request")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString("Jane Doe")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testRequestFriendship() throws Exception {
        var friendRequests = TestData.getFriendRequest();

        when(friendService.requestFriendship(
                eq("jane_girl18"),
                eq("john@doe123")))
                .thenReturn(friendRequests.get(0));

        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", "jane_girl18"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("John Doe")))
                .andExpect(content().string(containsString("Jane Doe")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testRequestFriendshipAlreadyFriends() throws Exception {
        var friendRequests = TestData.getFriendRequest();

        when(friendService.requestFriendship(
                eq("jane_girl18"),
                eq("john@doe123")))
                .thenReturn(friendRequests.get(0));

        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", "jane_girl18"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("John Doe")))
                .andExpect(content().string(containsString("Jane Doe")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testRequestFriendshipWithSelf() throws Exception {
        mvc.perform(post(new URI("/api/friend/request"))
                .param("username", "john@doe123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testAcceptFriendship() throws Exception {
        var users = getUsers();

        when(friendService.acceptFriendship(
                eq("jane_girl18"),
                eq("john@doe123")))
                .thenReturn(users.get(1));

        mvc.perform(post(new URI("/api/friend/accept"))
                .param("username", "jane_girl18"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Jane Doe")));
    }

    @Test
    @WithMockUser(username = "john@doe123")
    public void testRemoveFriend() throws Exception {
        when(friendService.acceptFriendship(
                eq("jane_girl18"),
                eq("john@doe123")))
                .thenReturn(null);

        mvc.perform(delete(new URI("/api/friend/remove"))
                .param("username", "jane_girl18"))
                .andExpect(status().isNoContent());
    }


    private List<AppUser> getUsers() {
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


}
