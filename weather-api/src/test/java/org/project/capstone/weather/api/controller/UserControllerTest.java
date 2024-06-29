package org.project.capstone.weather.api.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.filter.UserSearchFilter;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.interceptor.UnitsRequestParameterInterceptor;
import org.project.capstone.weather.api.repository.LocationRepository;
import org.project.capstone.weather.api.service.LocationService;
import org.project.capstone.weather.api.service.UserService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
@DisplayName("User Controller Unit Tests")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends BaseControllerTest {

    private static final Integer USER_ID = 1;

    @MockBean
    private UserService userService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private UnitsRequestParameterInterceptor interceptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Test
    @DisplayName("Get All Users - Should Return List of UserResponse and Status 200")
    public void testGetAllUsers_whenProperlyRequested_shouldReturnUserResponses_andStatusOk() throws Exception {
        List<UserResponse> userResponses = List.of(
                new UserResponse(1, "some@mail.com", "firstname", "lastname", Role.USER, null),
                new UserResponse(2, "some2@mail.com", "firstname2", "lastname2", Role.ADMIN, null));
        when(userService.getAllUsers(any(UserSearchFilter.class), any(Pageable.class))).thenReturn(userResponses);

        mockMvc.perform(get("/weather-api/v1/users"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2)),
                        content().json("""
                                [
                                    {
                                        "id": 1,
                                        "email": "some@mail.com",
                                        "firstname": "firstname",
                                        "lastname": "lastname",
                                        "role": "USER"
                                    },
                                    {
                                        "id": 2,
                                        "email": "some2@mail.com",
                                        "firstname": "firstname2",
                                        "lastname": "lastname2",
                                        "role": "ADMIN"
                                    }
                                ]
                                """)
                )
                .andReturn();

        verify(userService, times(1)).getAllUsers(any(UserSearchFilter.class), any(Pageable.class));
        verifyNoInteractions(interceptor);
    }

    @Test
    @DisplayName("Get User By ID - Should Return Proper UserResponse and Status 200")
    public void testGetUserById_whenUserExists_shouldReturnProperUserResponse_andStatusOk() throws Exception {
        UserResponse userResponse = new UserResponse(
                1,
                "some@mail.com",
                "firstname",
                "lastname",
                Role.USER,
                null);

        when(userService.getUserById(USER_ID)).thenReturn(Optional.of(userResponse));

        mockMvc.perform(get("/weather-api/v1/users/{userId}", USER_ID))
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                    {
                                        "id": 1,
                                        "email": "some@mail.com",
                                        "firstname": "firstname",
                                        "lastname": "lastname",
                                        "role": "USER"
                                    }
                                """)
                );

        verify(userService, times(1)).getUserById(any(Integer.class));
    }

    @Test
    @DisplayName("Get User By ID - Should Return 404 if User Does Not Exist")
    public void testGetUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(USER_ID)).thenReturn(Optional.empty());


        mockMvc.perform(get("/weather-api/v1/users/{userId}", USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(integerArgumentCaptor.capture());

        Assertions.assertThat(integerArgumentCaptor.getValue()).isEqualTo(USER_ID);

    }

    @Test
    @DisplayName("Create User - Should Return Proper User Response and Status 201")
    public void testCreateUser_whenValidUserDataProvided_shouldReturnProperUserResponse_andStatusCreated() throws Exception {
        UserResponse userResponse = new UserResponse(
                1,
                "new_user@mail.com",
                "firstname",
                "lastname",
                Role.USER,
                null);

        when(userService.createUser(any(UserCreateEditRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/weather-api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new_user@mail.com",
                                    "password": "password12345",
                                    "firstname": "firstname",
                                    "lastname": "lastname",
                                    "role": "USER"
                                }
                                """).accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", "http://localhost/weather-api/v1/users/1"),
                        content().json("""
                                    {
                                        "id": 1,
                                        "email": "new_user@mail.com",
                                        "firstname": "firstname",
                                        "lastname": "lastname",
                                        "role": "USER"
                                    }
                                """)
                );

        verify(userService, times(1)).createUser(any(UserCreateEditRequest.class));
    }

    @Test
    @DisplayName("Create User - Should Return 400 When No Valid Data Provided")
    public void testCreateUser_whenInvalidUserDataProvided_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/weather-api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .locale(new Locale("ru"))
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.error").isArray(),
                        jsonPath("$.error").isNotEmpty(),
                        content().json("""
                                {
                                    "error" : [
                                            "Пароль не может состоять только из пробелов",
                                            "Email обязателен и не может быть пустым",
                                            "Имя не может состоять только из пробелов",
                                            "Пароль обязателен для заполнения",
                                            "Фамилия не может состоять только из пробелов",
                                            "Необходимо указать роль пользователя"
                                        ]
                                }
                                """)
                );

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Update User - Should Return Proper UserResponse and Status 200")
    public void testUpdateUser_whenValidUserDataProvided_shouldReturnProperUserResponse_andStatusOk() throws Exception {
        UserResponse userResponse = new UserResponse(
                1,
                "new_user@mail.com",
                "firstname",
                "lastname",
                Role.USER,
                null);
        when(userService.updateUser(eq(USER_ID), any(UserCreateEditRequest.class))).thenReturn(Optional.of(userResponse));

        mockMvc.perform(put("/weather-api/v1/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "email": "updated@mail.com",
                                        "password": "password12345",
                                        "firstname": "firstname",
                                        "lastname": "lastname",
                                        "role": "USER"
                                }
                                """)
                )
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(eq(USER_ID), any(UserCreateEditRequest.class));
    }

    @Test
    @DisplayName("Update User - Should Return 400 When No Valid Data Provided")
    public void testUpdateUser_whenInvalidDataProvided_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/weather-api/v1/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").isArray(),
                        jsonPath("$.error").isNotEmpty()
                );

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Update User - Should Return 404 When User Does Not Exist")
    public void testUpdateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(USER_ID), any(UserCreateEditRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/weather-api/v1/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "email": "updated@mail.com",
                                        "password": "password12345",
                                        "firstname": "firstname",
                                        "lastname": "lastname",
                                        "role": "USER"
                                }
                                """)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                        .isInstanceOf(ResponseStatusException.class)
                        .hasMessage("404 NOT_FOUND"));

        verify(userService, times(1)).updateUser(eq(USER_ID), any(UserCreateEditRequest.class));
    }

    @Test
    @DisplayName("Delete User - Should Return 204 When User Exists")
    public void testDeleteUser_whenUserExists_shouldReturnNoContent() throws Exception {
        when(userService.deleteUser(USER_ID)).thenReturn(true);

        mockMvc.perform(delete("/weather-api/v1/users/{userId}", USER_ID))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(userService, times(1)).deleteUser(integerArgumentCaptor.capture());

        Assertions.assertThat(integerArgumentCaptor.getValue()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("Update User - Should Return 400 When No Valid Data Provided")
    public void testDeleteUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        when(userService.deleteUser(USER_ID)).thenReturn(false);

        mockMvc.perform(delete("/weather-api/v1/users/{userId}", USER_ID))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(userService, times(1)).deleteUser(USER_ID);
    }
}