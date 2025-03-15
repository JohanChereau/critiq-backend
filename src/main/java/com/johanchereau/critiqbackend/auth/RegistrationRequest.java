package com.johanchereau.critiqbackend.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;

    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, message = "Username should be at least 3 characters long")
    @Size(max = 20, message = "Username should be at most 20 characters long")
    private String username;

    @Email(message = "Email is not valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;

    @NotNull(message = "Terms accepted is mandatory")
    private Boolean termsAccepted;

    @NotNull(message = "Newsletter accepted is mandatory")
    private Boolean newsletterAccepted;
}
