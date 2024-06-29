package org.project.capstone.weather.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.auth.AuthenticationRequest;
import org.project.capstone.weather.api.dto.auth.AuthenticationResponse;
import org.project.capstone.weather.api.dto.auth.RegistrationRequest;
import org.project.capstone.weather.api.excpetion.UserAlreadyExistsException;
import org.project.capstone.weather.api.service.UserAuthenticationService;
import org.project.capstone.weather.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/weather-api/v1/auth")
@RequiredArgsConstructor
public class UserAuthenticationController {

    private final UserAuthenticationService authenticationService;

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest,
                                          UriComponentsBuilder uriComponentsBuilder) {

        userService.getUserByEmail(registrationRequest.email()).ifPresent(userResponse ->
        {
            throw new UserAlreadyExistsException();
        });

        UserResponse userResponse = authenticationService.register(registrationRequest);

        return ResponseEntity.created(uriComponentsBuilder
                        .replacePath("/weather-api/v1/users/{userId}")
                        .build(Map.of("userId", userResponse.id())))
                .body(userResponse);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
