package org.project.capstone.weather.api.integration.service;

import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.dto.LocationResponse;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.filter.UserSearchFilter;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.integration.IntegrationTestBase;
import org.project.capstone.weather.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

public class UserServiceIT extends IntegrationTestBase {

    private static final Integer USER_ID = 1;

    @Autowired
    private UserService userService;

    @Test
    void testGetAllUsers_shouldReturnUsersList() {
        UserSearchFilter filter = new UserSearchFilter(null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        List<UserResponse> expectedUsers = List.of(
                new UserResponse(1,
                        "john.doe@example.com",
                        "John",
                        "Doe",
                        Role.ADMIN,
                        List.of(new LocationResponse(1, "USA", "New York"), new LocationResponse(2, "USA", "Los Angeles"))),
                new UserResponse(2,
                        "jane.smith@example.com",
                        "Jane",
                        "Smith",
                        Role.USER,
                        List.of(new LocationResponse(3, "UK", "London")))
        );

        List<UserResponse> actualUsers = userService.getAllUsers(filter, pageable);

        assertThat(actualUsers).hasSize(expectedUsers.size());
        assertThat(actualUsers).isEqualTo(expectedUsers);

    }

    @Test
    public void testGetUserById_whenUserExists_shouldReturnOptionalWithUserResponse() {
        UserResponse userResponse = new UserResponse(
                1,
                "john.doe@example.com",
                "John",
                "Doe",
                Role.ADMIN,
                List.of(
                        new LocationResponse(1, "USA", "New York"),
                        new LocationResponse(2, "USA", "Los Angeles")
                ));

        Optional<UserResponse> actualUser = userService.getUserById(1);

        assertThat(actualUser).isPresent();
        actualUser.ifPresent(actual -> assertThat(actual).isEqualTo(userResponse));
    }

    @Test
    public void testGetUserById_whenUserDoesNotExist_shouldReturnEmptyOptional() {

        Optional<UserResponse> actualUserOpt = userService.getUserById(15);

        assertThat(actualUserOpt).isNotPresent();
    }

    @Test
    public void testCreateUser_whenValidUserCreationRequestProvided_shouldReturnUserResponse() {
        UserCreateEditRequest request = new UserCreateEditRequest(
                "new_email@mail.com",
                "12345password",
                "Tom",
                "Test",
                Role.USER
        );
        UserResponse expected = new UserResponse(
                3,
                "new_email@mail.com",
                "Tom",
                "Test",
                Role.USER,
                null
        );

        UserResponse actualUserResponse = userService.createUser(request);

        assertThat(actualUserResponse)
                .returns(expected.id(), from(UserResponse::id))
                .returns(expected.email(), from(UserResponse::email))
                .returns(expected.firstname(), from(UserResponse::firstname))
                .returns(expected.lastname(), from(UserResponse::lastname))
                .returns(expected.role(), from(UserResponse::role));
    }

    @Test
    public void testUpdateUser_whenUserExists_shouldReturnProperlyUpdatedUserResponse() {
        UserCreateEditRequest request = new UserCreateEditRequest(
                "new_email@mail.com",
                "password123",
                "John",
                "Doe",
                Role.ADMIN
        );
        UserResponse expectedResponse = new UserResponse(
                1,
                "new_email@mail.com",
                "John",
                "Doe",
                Role.ADMIN,
                null
        );

        Optional<UserResponse> actualResponse = userService.updateUser(USER_ID, request);

        actualResponse.ifPresent(actual -> assertThat(actual)
                .returns(expectedResponse.id(), from(UserResponse::id))
                .returns(expectedResponse.email(), from(UserResponse::email))
                .returns(expectedResponse.firstname(), from(UserResponse::firstname))
                .returns(expectedResponse.lastname(), from(UserResponse::lastname))
                .returns(expectedResponse.role(), from(UserResponse::role))
        );
    }

    @Test
    public void testUpdateUser_whenUserDoesNotExists_shouldReturnProperlyUpdatedUserResponse() {
        UserCreateEditRequest request = new UserCreateEditRequest(
                "new_email@mail.com",
                "password123",
                "John",
                "Doe",
                Role.ADMIN
        );

        Optional<UserResponse> actualResponse = userService.updateUser(15, request);

        assertThat(actualResponse).isNotPresent();
    }

    @Test
    public void testDeleteUser_whenUserExists_shouldReturnTrue() {

        boolean actualResult = userService.deleteUser(USER_ID);

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testDeleteUser_whenUserDoesNotExist_shouldReturnFalse() {

        boolean actualResult = userService.deleteUser(15);

        assertThat(actualResult).isFalse();
    }
}
