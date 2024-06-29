package org.project.capstone.weather.api.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.filter.UserSearchFilter;
import org.project.capstone.weather.api.service.UserService;
import org.project.capstone.weather.api.validation.CreateAction;
import org.project.capstone.weather.api.validation.UpdateAction;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather-api/v1/users")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public List<UserResponse> getAllUsers(UserSearchFilter filter, Pageable pageable) {
        return userService.getAllUsers(filter, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (authentication.principal.username == @userService.getUsernameById(#id))")
    public UserResponse getUserById(@PathVariable("id") @P("id") Integer id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Validated({Default.class, CreateAction.class}) UserCreateEditRequest userCreateEditDto,
                                        UriComponentsBuilder uriComponentsBuilder) {
        UserResponse userResponse = userService.createUser(userCreateEditDto);
        return ResponseEntity.created(uriComponentsBuilder
                        .replacePath("/weather-api/v1/users/{userId}")
                        .build(Map.of("userId", userResponse.id())))
                .body(userResponse);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable("userId") Integer id, @RequestBody @Validated({Default.class, UpdateAction.class}) UserCreateEditRequest userCreateEditDto) {
        return userService.updateUser(id, userCreateEditDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Integer id) {
        if (!userService.deleteUser(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}