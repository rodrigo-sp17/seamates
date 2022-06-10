package com.github.rodrigo_sp17.mscheduler.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationService;
import com.github.rodrigo_sp17.mscheduler.user.data.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager manager;
    private final ObjectMapper mapper;
    private final AuthenticationService authenticationService;

    public JWTAuthenticationFilter(AuthenticationManager manager,
                                   AuthenticationService authenticationService) {
        this.manager = manager;
        this.authenticationService = authenticationService;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserDTO user = mapper.readValue(request.getReader(), UserDTO.class);
            var token = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword());

            setDetails(request, token);
            return manager.authenticate(token);
        } catch (IOException e) {
            log.error("Could not read user from request");
            throw new AuthenticationServiceException(e.getMessage());
        } catch (AuthenticationException a) {
            log.info("Failed authentication attempt: " + a);
            throw a;
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        User user = (User) authResult.getPrincipal();
        var username = user.getUsername();
        String jwtToken = authenticationService.login(username);
        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
