package org.project.capstone.weather.api.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.UserCreateEditRequest;
import org.project.capstone.weather.api.dto.UserResponse;
import org.project.capstone.weather.api.dto.filter.UserSearchFilter;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.entity.UserEntity;
import org.project.capstone.weather.api.mapper.UserCreateEditMapper;
import org.project.capstone.weather.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Integer USER_ID = 1;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserCreateEditMapper userCreateEditMapper = Mappers.getMapper(UserCreateEditMapper.class);


    @Test
    void testGetAllUsers() {
        // Arrange
        UserEntity user1 = UserEntity.builder()
                .id(USER_ID)
                .email("user1@testmail.com")
                .password("testpassword")
                .firstname("Test User1")
                .lastname("Test User1")
                .role(Role.USER)
                .userLocations(Collections.emptyList())
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(USER_ID)
                .email("user2@testmail.com")
                .password("testpassword")
                .firstname("Test User2")
                .lastname("Test User2")
                .role(Role.ADMIN)
                .userLocations(Collections.emptyList())
                .build();

        List<UserEntity> users = Arrays.asList(user1, user2);

        UserResponse userResponse1 = userCreateEditMapper.userToUserResponse(user1);
        UserResponse userResponse2 = userCreateEditMapper.userToUserResponse(user2);
        List<UserResponse> expectedUserResponses = Arrays.asList(userResponse1, userResponse2);

        Page<UserEntity> pagedUsers = new PageImpl<>(users);
        UserSearchFilter filter = new UserSearchFilter(null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Predicate.class), eq(pageable))).thenReturn(pagedUsers);
        when(userCreateEditMapper.userToUserResponse(user1)).thenReturn(userResponse1);
        when(userCreateEditMapper.userToUserResponse(user2)).thenReturn(userResponse2);

        // Act
        List<UserResponse> actualUserResponses = userService.getAllUsers(filter, pageable);

        // Assert
        assertThat(actualUserResponses).hasSize(expectedUserResponses.size());
        assertThat(actualUserResponses).isEqualTo(expectedUserResponses);
        verify(userRepository, times(1)).findAll(any(Predicate.class), eq(pageable));
        verify(userCreateEditMapper, times(3)).userToUserResponse(user1);
        verify(userCreateEditMapper, times(3)).userToUserResponse(user2);
    }

    @Test
    public void testGetUserById_whenUserExists() {
        // Arrange
        UserEntity mockUserEntity = UserEntity.builder().id(USER_ID).build();
        UserResponse userResponse = userCreateEditMapper.userToUserResponse(mockUserEntity);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUserEntity));
        when(userCreateEditMapper.userToUserResponse(mockUserEntity)).thenReturn(userResponse);

        // Act
        Optional<UserResponse> actualUser = userService.getUserById(USER_ID);

        // Assert
        assertThat(actualUser).isPresent();
        actualUser.ifPresent(actual -> assertThat(actual).isEqualTo(userResponse));

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userCreateEditMapper, times(3)).userToUserResponse(mockUserEntity);
    }

    @Test
    public void testGetUserById_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponse> actualUserResponse = userService.getUserById(USER_ID);

        // Assert
        assertThat(actualUserResponse).isEmpty();
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userCreateEditMapper, never()).userToUserResponse(any());
    }

    @Test
    public void testCreateUser_whenUserCreateEditRequestIsProvided_shouldReturnUserResponse() {
        // Arrange
        UserCreateEditRequest userCreateEditRequest = new UserCreateEditRequest(
                "test@mail.com",
                "test_password",
                "Test Firstname",
                "Test Lastname",
                Role.USER);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        UserResponse expectedUserResponse = new UserResponse(
                USER_ID,
                "test@mail.com",
                "Test Firstname",
                "Test Lastname",
                Role.USER,
                null);

        when(userCreateEditMapper.userCreateEditToUser(userCreateEditRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userCreateEditMapper.userToUserResponse(userEntity)).thenReturn(expectedUserResponse);

        // Act
        UserResponse actualUserResponse = userService.createUser(userCreateEditRequest);

        // Assert
        assertThat(actualUserResponse)
                .isEqualTo(expectedUserResponse)
                .describedAs("The user response should match the expected response");

        verify(userCreateEditMapper, times(1)).userCreateEditToUser(userCreateEditRequest);
        verify(userRepository, times(1)).save(userEntity);
        verify(userCreateEditMapper, times(2)).userToUserResponse(userEntity);
    }

    @Test
    public void testUpdateUser_whenUserExists() {
        // Arrange
        UserCreateEditRequest createEditRequest = new UserCreateEditRequest(
                "john.doe@example.com",
                "testpassword",
                "John",
                "Doe",
                Role.USER);

        UserEntity existingUserEntity = new UserEntity();
        UserEntity updatedUserEntity = new UserEntity();
        UserResponse expectedResponse = new UserResponse(
                USER_ID,
                "john.doe@example.com",
                "John",
                "Doe",
                Role.USER,
                null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUserEntity));
        when(userCreateEditMapper.update(createEditRequest, existingUserEntity)).thenReturn(updatedUserEntity);
        when(userRepository.saveAndFlush(updatedUserEntity)).thenReturn(updatedUserEntity);
        when(userCreateEditMapper.userToUserResponse(updatedUserEntity)).thenReturn(expectedResponse);

        // Act
        Optional<UserResponse> actualResponse = userService.updateUser(USER_ID, createEditRequest);

        // Assert
        assertThat(actualResponse)
                .isPresent()
                .contains(expectedResponse);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userCreateEditMapper, times(2)).update(createEditRequest, existingUserEntity);
        verify(userRepository, times(1)).saveAndFlush(updatedUserEntity);
        verify(userCreateEditMapper, times(2)).userToUserResponse(updatedUserEntity);
    }

    @Test
    public void testUpdateUser_whenUserDoesNotExist() {
        // Arrange
        UserCreateEditRequest createEditRequest = new UserCreateEditRequest(
                "john.doe@example.com",
                "testpassword",
                "John",
                "Doe",
                Role.USER);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponse> actualResponse = userService.updateUser(USER_ID, createEditRequest);

        // Assert
        assertThat(actualResponse).isEmpty();

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userCreateEditMapper, userRepository);
    }

    @Test
    public void testDeleteUser_whenUserExists() {
        // Arrange
        UserEntity existingUser = new UserEntity();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));

        // Act
        boolean actualResult = userService.deleteUser(USER_ID);

        // Assert
        assertThat(actualResult).isTrue();

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    public void testDeleteUser_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        boolean actualResult = userService.deleteUser(USER_ID);

        // Assert
        assertThat(actualResult).isFalse();

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);
    }
}