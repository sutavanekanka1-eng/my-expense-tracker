package org.example.model;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDto {

    @Size(min=6)
    private String password;

    @NotBlank
    private String username;

    @NotBlank
    private String lastname;

    @Size(min=10, max=10)
    private long phoneNumber;

    @Email
    private String email;
}