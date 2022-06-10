package com.github.rodrigo_sp17.mscheduler.user.data;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * Represents a request for changing the password of an account
 */
@Data
public class PasswordRequestDTO {

    private String username;
    @Size(min = 8, max = 128,
            message = "Passwords must be between 8 and 128 characters long")
    private String password;
    private String confirmPassword;

}
