package com.github.rodrigo_sp17.mscheduler.service;

import com.github.rodrigo_sp17.mscheduler.auth.OAuth2Service;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OAuth2ServiceTest {
    private final String fbSecret = "fbsecret";

    private final OAuth2Service oAuth2Service = new OAuth2Service(fbSecret);

    @Test
    public void test_encodeDecode() {
        var token = oAuth2Service.getSignedRequest(Map.of("username", "somethings"));
        var json = oAuth2Service.parseSignedRequest(token);
        var decodedUsername = json.get("username");
        assertEquals(decodedUsername, "somethings");
    }
}
