package com.github.rodrigo_sp17.mscheduler.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final AuthenticationService authenticationService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        var token = parseToken(header);
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String username;
        try {
            username = authenticationService.verifyJWTToken(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }

        if (username == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(username,
                null,
                Collections.emptyList());
    }
}
