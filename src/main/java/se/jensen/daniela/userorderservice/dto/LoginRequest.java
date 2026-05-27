package se.jensen.daniela.userorderservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
