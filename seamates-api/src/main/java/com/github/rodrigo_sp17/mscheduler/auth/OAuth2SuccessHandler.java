package com.github.rodrigo_sp17.mscheduler.auth;

import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredentialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This handler is responsible for processing information from a successful OAuth2 login,
 * integrating it with the current authentication schema.
 */
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final SocialCredentialRepository socialCredentialRepository;
    private final AuthenticationService authenticationService;

    public OAuth2SuccessHandler(SocialCredentialRepository socialCredentialRepository,
                                AuthenticationService authenticationService) {
        this.socialCredentialRepository = socialCredentialRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var token = (OAuth2AuthenticationToken) authentication;
        var socialId = authentication.getName();
        var registrationId = token.getAuthorizedClientRegistrationId();

        var credential = socialCredentialRepository
                .findBySocialIdAndRegistrationId(socialId, registrationId);

        if (credential.isPresent()) {
            var user = credential.get().getSocialUser();
            var username = user.getUserInfo().getUsername();

            // Logs user in
            String jwtToken = authenticationService.login(username);

            // Returns page with JWT to user
            String url = String.format("/loginSuccess?token=%s&user=%s",
                    jwtToken,
                    username);

            response.sendRedirect(url);
        } else {
            var oAuth2User = (OAuth2User) authentication.getPrincipal();
            String name = oAuth2User.getAttribute("name");
            String email = oAuth2User.getAttribute("email");

            // Redirect to new signup
            String url = String.format("/socialSignup?name=%s&email=%s&socialId=%s&registrationId=%s",
                    name,
                    email,
                    socialId,
                    registrationId);

            response.sendRedirect(url);
        }
    }
}
