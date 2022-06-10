package com.github.rodrigo_sp17.mscheduler.auth.data;

import com.github.rodrigo_sp17.mscheduler.auth.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuthenticationService authenticationService;

    public JWTLogoutSuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest req,
                                HttpServletResponse res,
                                Authentication authentication) {
        var bearerToken = req.getHeader("Authorization");
        var token = bearerToken.replace("Bearer ", "");
        if (authenticationService.logout(token)) {
            res.setStatus(HttpStatus.OK.value());
        } else {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
