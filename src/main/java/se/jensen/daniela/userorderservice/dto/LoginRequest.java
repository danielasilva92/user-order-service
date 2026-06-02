package se.jensen.daniela.userorderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Användarnamn krävs")
    private String username;

    @NotBlank(message = "Lösenord krävs")
    private String password;
}
