package com.github.rodrigo_sp17.mscheduler.friend;

import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequest;
import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequestRepository;
import com.github.rodrigo_sp17.mscheduler.friend.exception.FriendRequestNotFoundException;
import com.github.rodrigo_sp17.mscheduler.push.PushService;
import com.github.rodrigo_sp17.mscheduler.push.events.FriendAcceptEvent;
import com.github.rodrigo_sp17.mscheduler.push.events.FriendRequestEvent;
import com.github.rodrigo_sp17.mscheduler.push.events.PushEvent;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendService {
    @Autowired
    private final FriendRequestRepository requestRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final PushService pushService;

    public FriendService(FriendRequestRepository requestRepository,
                         PushService pushService,
                         UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.pushService = pushService;
    }

    public List<AppUser> getFriendsByUser(String username) {
        AppUser user = userService.getUserByUsername(username);
        return user.getFriends();
    }

    public List<FriendRequest> getFriendRequestsForUser(String username) {
        return requestRepository.findRequestsContaining(username);
    }

    public FriendRequest getRequestById(Long requestId, String owner) {
        FriendRequest request = requestRepository.findByIdAndSourceUsername(requestId, owner);
        if (request == null) {
            throw new FriendRequestNotFoundException("Friend request not found for requester");
        }
        return request;
    }

    @Transactional
    public FriendRequest requestFriendship(String friendName, String username) {
        // check if there is a request already
        AppUser user = userService.getUserByUsername(username);

        List<FriendRequest> requests = requestRepository.findRequestsContaining(username);
        List<Long> requestIds = requests.stream()
                .filter(fr -> fr.getTarget().getUserInfo().getUsername().equals(friendName)
                            || fr.getSource().getUserInfo().getUsername().equals(friendName))
                .map(FriendRequest::getId)
                .collect(Collectors.toList());

        if (!requestIds.isEmpty()) {
            if (requestIds.size() == 1) {
                return requestRepository.findById(requestIds.get(0))
                        .orElseThrow(FriendRequestNotFoundException::new);
            } else {
                log.error("2 requests for same username were detected");
                throw new AssertionError("There should not be more than 1 request " +
                        "between users");
            }
        }

        // checks if there is a friendship already
        if (user.getFriends().stream()
                .map(u -> u.getUserInfo().getUsername())
                .anyMatch(name -> name.equals(friendName))) {
            throw new IllegalArgumentException("Cannot request friendship to a friend");
        }

        // if not, build request, save and return
        AppUser friend = userService.getUserByUsername(friendName);
        var friendRequest = new FriendRequest();
        friendRequest.setSource(user);
        friendRequest.setTarget(friend);
        friendRequest.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));

        // persists all
        var result = requestRepository.save(friendRequest);

        log.info("Friend request created");
        sendRequestEvent(friendName, username);

        return result;
    }

    @Transactional
    public AppUser acceptFriendship(String friendName, String username) {
        FriendRequest request = requestRepository.findBySourceUsernameAndTargetUsername(
                friendName,
                username);
        if (request == null) {
            throw new FriendRequestNotFoundException();
        }

        AppUser friend = request.getSource();
        AppUser user = request.getTarget();

        // Mutually adds friends
        user.getFriends().add(friend);
        AppUser savedUser = userService.saveUser(user);
        friend.getFriends().add(savedUser);

        // Removes request since friendship was established
        requestRepository.deleteById(request.getId());

        sendAcceptEvent(friendName, username);

        return friend;
    }

    @Transactional
    public void removeFriendByUsername(String friendUsername, String username) {
        AppUser owner = userService.getUserByUsername(username);
        AppUser friend = userService.getUserByUsername(friendUsername);

        if (owner.getFriends().contains(friend)) {
            owner.getFriends().remove(friend);
            friend.getFriends().remove(owner);
        } else {
            throw new UserNotFoundException("Not a friend");
        }

        userService.saveUser(owner);
        userService.saveUser(friend);
        log.info("Friendship removed");
    }

    private void sendRequestEvent(String friendName, String username) {
        PushEvent event = new FriendRequestEvent(username, friendName);
        pushService.pushNotification(friendName, event);
        pushService.pushNotification(username, event);
    }

    private void sendAcceptEvent(String friendName, String username) {
        PushEvent event = new FriendAcceptEvent(username, friendName);
        pushService.pushNotification(friendName, event);
        pushService.pushNotification(username, event);
    }
}
