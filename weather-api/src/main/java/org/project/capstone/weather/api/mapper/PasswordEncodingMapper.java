package org.project.capstone.weather.api.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncodingMapper {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @EncodedMapping
    public static String encode(String value) {
        return value.startsWith("{bcrypt}") ? value.substring(8) : passwordEncoder.encode(value);
    }
}
