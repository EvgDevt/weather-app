package org.project.capstone.weather.api.mapper;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.entity.UserEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserCreateEditMapperTest {

    private final UserCreateEditMapper userMapper = new UserCreateEditMapperImpl();


    @Test
    public void testUserToUserResponse_whenUserInitializedOnlyWithId_shouldProperlyMapToUserResponse() {
        UserEntity user = UserEntity.builder().id(1).build();
        UserResponse expectedUserResponse = new UserResponse(1, null, null, null, null, Collections.emptyList());

        UserResponse actualUserResponse = userMapper.userToUserResponse(user);

        assertEquals(actualUserResponse, expectedUserResponse);
    }

    @Test
    public void testUserToUserResponse_shouldProperlyMapUserCreateEditRequestToUser() {
        UserCreateEditRequest userCreateEditRequest = new UserCreateEditRequest(
                "test@mail.com",
                "test",
                "FirstNameTest",
                "LastNameTest",
                Role.USER);


        UserEntity user = userMapper.userCreateEditToUser(userCreateEditRequest);

        assertNotNull(user);
        assertEquals(userCreateEditRequest.email(), user.getEmail());
        assertEquals(userCreateEditRequest.firstname(), user.getFirstname());
        assertEquals(userCreateEditRequest.lastname(), user.getLastname());
        assertEquals(userCreateEditRequest.role(), user.getRole());
    }

    @Test
    public void testUserCreateEditToUser_whenRoleProvided_shouldNotSetDefaultRoleAfterMapping() {
        UserCreateEditRequest userRegistrationDto = new UserCreateEditRequest(
                "test@mail.com",
                "test",
                "FirstNameTest",
                "LastNameTest",
                Role.ADMIN);

        UserEntity user = userMapper.userCreateEditToUser(userRegistrationDto);

        assertNotNull(user);
        assertEquals(userRegistrationDto.email(), user.getEmail());
        assertNotEquals("USER", user.getRole().name());
    }

    @Test
    public void testUserToUserResponse_shouldProperlyMapUserToUserResponseDto() {
        UserEntity user = UserEntity.builder()
                .id(1)
                .email("test@mail.com")
                .firstname("Test Firstname")
                .lastname("Test Lastname")
                .password("test")
                .role(Role.USER)
                .build();

        UserResponse userResponseDto = userMapper.userToUserResponse(user);

        assertNotNull(userResponseDto);
        assertNotNull(userResponseDto.id());

        assertEquals(userResponseDto.firstname(), user.getFirstname());
        assertEquals(userResponseDto.role(), user.getRole());
    }

    @Test
    public void testUpdate_shouldProperlyMapWhenUpdateUser() {
        UserCreateEditRequest userUpdateDto = new UserCreateEditRequest(
                "test@mail.com",
                "testpassword",
                "Test Firstname",
                "Test Lastname",
                Role.ADMIN);

        UserEntity user = UserEntity.builder()
                .email("test@mila.com")
                .password("testpassword")
                .firstname("Test Firstname")
                .lastname("Test lastname")
                .role(Role.USER)
                .build();

        UserEntity updatedUser = userMapper.update(userUpdateDto, user);

        assertNotNull(updatedUser);
        Assertions.assertThat(updatedUser.getRole()).isSameAs(userUpdateDto.role());
        Assertions.assertThat(updatedUser.getRole()).isNotEqualTo(Role.USER);
    }
}
