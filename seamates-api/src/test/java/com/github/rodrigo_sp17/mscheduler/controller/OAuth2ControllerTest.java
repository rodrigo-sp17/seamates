package com.github.rodrigo_sp17.mscheduler.controller;

import com.github.rodrigo_sp17.mscheduler.TestData;
import com.github.rodrigo_sp17.mscheduler.auth.OAuth2Service;
import com.github.rodrigo_sp17.mscheduler.auth.OAuth2Controller;
import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredential;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = OAuth2Controller.class)
public class OAuth2ControllerTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private OAuth2Service oAuth2Service;

    private final String testToken = "rFGH4aFz0sE8uc0HQPzq9OEHbDlV283IaetialJi0KQ=" +
            ".eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsInVzZXJfaWQiOiIxMjM0NSJ9";

    @Test
    public void test_delete() throws Exception {
        var user = TestData.getUsers().get(0);
        var credential = new SocialCredential();
        credential.setId(1L);
        credential.setSocialUser(user);
        credential.setRegistrationId("anyt");
        credential.setSocialId("12345");

        when(socialCredentialRepository.findBySocialIdAndRegistrationId(any(), any()))
                .thenReturn(Optional.of(credential));
        when(oAuth2Service.parseSignedRequest(any())).thenReturn(Map.of("user_id", "112233"));
        when(oAuth2Service.getSignedRequest(any())).thenReturn("somerandomtoken");

        mvc.perform(post(new URI("/oauth2/delete")).param("signed_request", testToken))
                .andExpect(jsonPath("$.url").hasJsonPath())
                .andExpect(jsonPath("$.confirmation_code").hasJsonPath())
                .andExpect(content().string(containsString("http")));
    }
}
