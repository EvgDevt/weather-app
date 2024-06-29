package org.project.capstone.weather.api.mapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.capstone.weather.api.dto.auth.RegistrationRequest;
import org.project.capstone.weather.api.entity.Role;
import org.project.capstone.weather.api.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationMapperTest {

    @InjectMocks
    private final UserRegistrationMapper userRegistrationMapper = new UserRegistrationMapperImpl();

    @Mock
    private PasswordEncodingMapper passwordEncodingMapper;


    @Test
    @Disabled
    public void testSetDefaultRoleAfterUserRegistration_whenInitialUserRegistrationMappingCompleted() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                "email@mail.com",
                "password12345",
                "Firstname",
                "Lastname");

        // Mock the password encoding behavior
        when(PasswordEncodingMapper.encode("password12345")).thenReturn("encodedPassword12345");

        // Act
        UserEntity userEntity = userRegistrationMapper.registrationRequestToUser(request);

        // Assert
        assertEquals("email@mail.com", userEntity.getEmail());
        assertEquals("encodedPassword12345", userEntity.getPassword());
        assertEquals("Firstname", userEntity.getFirstname());
        assertEquals("Lastname", userEntity.getLastname());
        assertEquals(Role.USER, userEntity.getRole());
    }
}
