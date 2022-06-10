package com.github.rodrigo_sp17.mscheduler.user.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;

/**
 * Represents a User to be added, modified or deleted
 */
@Data
public class UserDTO extends RepresentationModel<UserDTO> {

    private Long userId;

    @Size(min = 6, max = 30, message = "Usernames must be between 6 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+([_@#&-]?[a-zA-Z0-9 ])*$",
            message = "Invalid username")
    private String username;

    @Size(min = 1, max = 150, message = "Names are mandatory and must have at most 150 characters")
    @Pattern(regexp = "^([^0-9{}\\\\/()\\]\\[]*)$", message = "Names must not contain numbers or \\/(){}[]")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email")
    private String email;

    @Size(min = 8, max = 128, message = "Passwords must be between 8 and 128 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

}
