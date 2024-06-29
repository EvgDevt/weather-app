package org.project.capstone.weather.api.integration.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.project.capstone.weather.api.integration.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WithMockUser(
        username = "user",
        password = "password1",
        authorities = {"USER", "ADMIN"}
)
public class UserControllerIT extends IntegrationTestBase {

    private static final Integer USER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllUsers_whenProperlyRequested_shouldReturnUserList_andStatusOk() throws Exception {
        mockMvc.perform(get("/weather-api/v1/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                        "id": 1,
                                        "email": "john.doe@example.com",
                                        "firstname": "John",
                                        "lastname": "Doe",
                                        "role": "ADMIN"
                                    },
                                    {
                                        "id": 2,
                                        "email": "jane.smith@example.com",
                                        "firstname": "Jane",
                                        "lastname": "Smith",
                                        "role": "USER"
                                    }
                                ]
                                """));
    }

    @Test
    public void testGetUserById_whenUserExists_shouldReturnUserResponse_andStatusOk() throws Exception {
        mockMvc.perform(get("/weather-api/v1/users/{userId}", USER_ID))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 1,
                                    "email": "john.doe@example.com",
                                    "firstname": "John",
                                    "lastname": "Doe",
                                    "role": "ADMIN"
                                }
                                """)
                );
    }

    @Test
    public void testGetUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/weather-api/v1/{userId}", 5))
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        status().isNotFound()
                );
    }

    @Test
    public void testCreateUser_whenValidUserDataProvided_shouldReturnUserResponse_andStatusCreated() throws Exception {
        mockMvc.perform(post("/weather-api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new_user@mail.com",
                                    "password": "new_password",
                                    "firstname": "newFirstname",
                                    "lastname": "newLastname",
                                    "role": "USER"
                                }
                                """))
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/weather-api/v1/users/3"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 3,
                                    "email": "new_user@mail.com",
                                    "firstname": "newFirstname",
                                    "lastname": "newLastname",
                                    "role": "USER"
                                }
                                                """)
                );
    }

    @Test
    public void testCreateUser_whenInvalidUserDataProvided_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/weather-api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "email_not_valid",
                                    "firstname": "name first",
                                    "lastname": "name last",
                                    "role": "USER"
                                }
                                """).locale(Locale.forLanguageTag("ru")))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "error": [
                                            "Пароль обязателен для заполнения",
                                            "Email имеет неверный формат",
                                            "Пароль не может состоять только из пробелов"
                                    ]
                                }
                                """),
                        result -> Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(MethodArgumentNotValidException.class)
                                .hasMessageStartingWith("Validation failed for argument")

                );
    }

    @Test
    public void testUpdateUser_whenUserExists_andValidRequestProvided_shouldReturnUserResponse() throws Exception {
        mockMvc.perform(put("/weather-api/v1/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new_mail@example.com",
                                    "password": "password123",
                                    "firstname": "John",
                                    "lastname": "Doe",
                                    "role": "ADMIN"
                                }
                                """)
                        .locale(Locale.forLanguageTag("ru"))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "email": "new_mail@example.com",
                                    "firstname": "John",
                                    "lastname": "Doe",
                                    "role": "ADMIN"
                                }
                                """)
                );
    }

    @Test
    public void testUpdateUser_whenUserExists_andInvalidRequestProvided_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/weather-api/v1/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john.doe@example.com",
                                    "firstname": "first name is too long and can't be passed any further",
                                    "lastname": "lastname",
                                    "role": "ADMIN"
                                }
                                """)
                        .locale(Locale.forLanguageTag("ru"))
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "error": [
                                            "Имя может быть от 1 до 12 символов"
                                    ]
                                }
                                """),
                        result -> Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(MethodArgumentNotValidException.class)
                                .hasMessageStartingWith("Validation failed for argument")
                );
    }

    @Test
    public void testUpdateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/weather-api/v1/users/{userId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "email@mail.com",
                                    "password": "password123",
                                    "firstname": "firstname",
                                    "lastname": "lastname",
                                    "role": "USER"
                                }
                                """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_whenUserExists_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/weather-api/v1/users/{userId}", USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/weather-api/users/{userId}", 10))
                .andExpect(status().isNotFound());
    }
}
