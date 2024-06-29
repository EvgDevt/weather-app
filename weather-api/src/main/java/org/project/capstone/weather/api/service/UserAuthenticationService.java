package org.project.capstone.weather.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.auth.AuthenticationRequest;
import org.project.capstone.weather.api.dto.auth.AuthenticationResponse;
import org.project.capstone.weather.api.dto.auth.RegistrationRequest;
import org.project.capstone.weather.api.mapper.UserCreateEditMapper;
import org.project.capstone.weather.api.mapper.UserRegistrationMapper;
import org.project.capstone.weather.api.repository.UserRepository;
import org.project.capstone.weather.api.security.JwtService;
import org.project.capstone.weather.api.security.UserEntityDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthenticationService {

    private final UserRepository userRepository;

    private final UserRegistrationMapper registrationMapper;

    private final UserCreateEditMapper userMapper;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    @Transactional
    public UserResponse register(RegistrationRequest request) {
        return Optional.of(request)
                .map(registrationMapper::registrationRequestToUser)
                .map(userRepository::save)
                .map(userMapper::userToUserResponse)
                .orElseThrow();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Map<String, Object> claims = new HashMap<>();
        UserEntityDetails user = (UserEntityDetails) auth.getPrincipal();

        log.info("User: {} is logging into the system. Success: {}. Access level: {}", user.getUsername(), auth.isAuthenticated(), auth.getAuthorities());

        claims.put("fullName", String.join(" ", user.getUserEntity().getFirstname(), user.getUserEntity().getLastname()));

        String jwt = jwtService.generateToken(claims, (UserEntityDetails) auth.getPrincipal());

        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }
}
