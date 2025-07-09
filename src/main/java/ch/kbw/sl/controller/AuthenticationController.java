package ch.kbw.sl.controller;

import ch.kbw.sl.entity.LoginResponse;
import ch.kbw.sl.entity.RegisterUserDto;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.AuthenticationService;
import ch.kbw.sl.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        log.info("Registering user with email: {}", registerUserDto.getEmail());
        User registeredUser = authenticationService.signup(registerUserDto);

        String jwtToken = jwtService.generateToken(registeredUser);
        log.info("Generated JWT token for user: {}", registeredUser.getEmail());
        registeredUser.setToken(jwtToken);
        registeredUser.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody RegisterUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        log.info("Generated JWT token for user: {}", authenticatedUser.getEmail());
        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check endpoint hit");
        return ResponseEntity.ok("Authentication service is running");
    }


}
