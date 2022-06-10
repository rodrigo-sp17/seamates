package com.github.rodrigo_sp17.mscheduler.security;

import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AppUser user;
        try {
            user = userService.getUserByUsername(s);
        } catch (UserNotFoundException e) {
            try {
                user = userService.getUserByEmail(s);
            } catch (UserNotFoundException d) {
                throw new UsernameNotFoundException("Username not found: " + s);
            }
        }

        return new User(user.getUserInfo().getUsername(),
                user.getUserInfo().getPassword(),
                Collections.emptyList());
    }
}
