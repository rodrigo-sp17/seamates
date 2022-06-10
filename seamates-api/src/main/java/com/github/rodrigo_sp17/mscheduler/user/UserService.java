package com.github.rodrigo_sp17.mscheduler.user;

import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequestRepository;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.data.UserRepository;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       FriendRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public List<String> getUsernames() {
        return userRepository.findUsernames();
    }


    public AppUser getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username) == null;
    }

    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email) == null;
    }

    public AppUser getUserByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Could not find user " + username);
        }
        return user;
    }

    public AppUser getUserByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Could not find user " + email);
        }
        return user;
    }

    @Transactional
    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(AppUser user) {
        var friends = user.getFriends();

        friends.forEach(f -> {
            f.getFriends().remove(user);
            userRepository.save(f);
        });

        userRepository.delete(user);
        requestRepository.deleteAllById(user.getUserId());
        return true;
    }
}
