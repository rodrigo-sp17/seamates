package com.github.rodrigo_sp17.mscheduler.auth.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * Represents the expected confirmation to a delete request to be received by Facebook
 */
@Data
@AllArgsConstructor
public class FacebookConfirmationDTO {
    private String url;
    @JsonProperty("confirmation_code")
    private String confirmationCode;
}
