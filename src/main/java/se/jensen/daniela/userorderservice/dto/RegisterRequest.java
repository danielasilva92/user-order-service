package se.jensen.daniela.userorderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Användarnamn krävs")
    @Size(min = 3, max = 30, message = "Användarnamn måste vara 3–30 tecken")
    private String username;

    @NotBlank(message = "E-post krävs")
    @Email(message = "Ogiltig e-postadress")
    private String email;

    @NotBlank(message = "Lösenord krävs")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$",
            message = "Lösenordet måste vara minst 6 tecken och innehålla minst en stor bokstav, en siffra och ett specialtecken"
    )
    private String password;
}
