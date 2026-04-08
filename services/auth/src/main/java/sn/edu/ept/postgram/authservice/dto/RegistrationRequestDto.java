package sn.edu.ept.postgram.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistrationRequestDto(
        String firstName,
        String lastName,

        @NotBlank(message = "Provide a valid email")
        String email,

        @NotBlank(message = "username cannot be blank")
        String username,

        @NotBlank(message = "password cannot be blank")
        String password,
        String bio) {
}