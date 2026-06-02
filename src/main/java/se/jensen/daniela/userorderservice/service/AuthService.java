package se.jensen.daniela.userorderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.jensen.daniela.userorderservice.dto.AuthResponse;
import se.jensen.daniela.userorderservice.dto.LoginRequest;
import se.jensen.daniela.userorderservice.dto.RegisterRequest;
import se.jensen.daniela.userorderservice.entity.User;
import se.jensen.daniela.userorderservice.repository.UserRepository;
import se.jensen.daniela.userorderservice.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Användarnamnet är redan taget.");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-posten är redan registrerad.");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return new AuthResponse(jwtUtil.generateToken(user.getUsername()), user.getUsername());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        return new AuthResponse(jwtUtil.generateToken(req.getUsername()), req.getUsername());
    }
}
