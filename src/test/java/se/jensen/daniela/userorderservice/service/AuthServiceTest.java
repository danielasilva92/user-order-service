package se.jensen.daniela.userorderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import se.jensen.daniela.userorderservice.dto.AuthResponse;
import se.jensen.daniela.userorderservice.dto.LoginRequest;
import se.jensen.daniela.userorderservice.dto.RegisterRequest;
import se.jensen.daniela.userorderservice.entity.User;
import se.jensen.daniela.userorderservice.repository.UserRepository;
import se.jensen.daniela.userorderservice.security.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("daniela");
        registerRequest.setEmail("daniela@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("daniela");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_sparar_användare_och_returnerar_token() {
        when(userRepository.existsByUsername("daniela")).thenReturn(false);
        when(userRepository.existsByEmail("daniela@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken("daniela")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("daniela", response.getUsername());
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_kastar_exception_om_användarnamn_redan_finns() {
        when(userRepository.existsByUsername("daniela")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Användarnamnet är redan taget.", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_kastar_exception_om_email_redan_finns() {
        when(userRepository.existsByUsername("daniela")).thenReturn(false);
        when(userRepository.existsByEmail("daniela@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("E-posten är redan registrerad.", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnerar_token_vid_korrekt_uppgifter() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtUtil.generateToken("daniela")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("daniela", response.getUsername());
        assertEquals("mocked-jwt-token", response.getToken());
    }
}
