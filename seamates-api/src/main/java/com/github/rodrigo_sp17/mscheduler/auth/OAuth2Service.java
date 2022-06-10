package com.github.rodrigo_sp17.mscheduler.auth;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class OAuth2Service {
    private final String facebookSecret;

    public OAuth2Service(@Value("${spring.security.oauth2.client.registration.facebook.client-secret}") String facebookSecret) {
        this.facebookSecret = facebookSecret;
    }

    /**
     * Parses a Facebook signed request consisting of [BASE64UL_SIGNATURE].[BASE64URL_PAYLOAD].
     * <p>
     * It assumes the secret is in plaintext when signing.
     *
     * @param signedRequest The signed request to parse, as a String
     * @return a Map representing the JSON contained in the payload if the signature is verified,
     * else null
     */
    public Map<String, Object> parseSignedRequest(String signedRequest) {
        var parts = signedRequest.split("\\.");
        var decodedSig =  Base64.getUrlDecoder().decode(parts[0]);
        var decodedPayload = Base64.getUrlDecoder().decode(parts[1]);

        try {
            var hmac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256,
                    facebookSecret.getBytes());
            hmac.update(parts[1].getBytes());

            var expectedSig = hmac.doFinal();
            if (!Arrays.equals(decodedSig, expectedSig)) {
                return null;
            }

            return JSONObjectUtils.parse(new String(decodedPayload));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates a Facebook-style signed request ([BASE64UL_SIGNATURE].[BASE64URL_PAYLOAD])
     * from a Map representing a JSON object.
     * <p>
     * The signature uses a plain-text secret.
     *
     * @param data a Map representing a JSON object to be included in the payload
     * @return The signed request as String, or null if any exception occurs
     */
    public String getSignedRequest(Map<String, Object> data) {
        var encoder = Base64.getUrlEncoder();
        try {
            var hmac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256,
                    facebookSecret.getBytes());

            var jsonData = JSONObjectUtils.toJSONString(data);
            var payload = encoder.encode(jsonData.getBytes());
            hmac.update(payload);
            var sig = encoder.encodeToString(hmac.doFinal());
            return sig + "." + new String(payload);
        } catch (Exception e) {
            log.warn(e.toString());
            return null;
        }
    }
}
