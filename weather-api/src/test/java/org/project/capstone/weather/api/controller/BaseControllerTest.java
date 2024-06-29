package org.project.capstone.weather.api.controller;

import org.project.capstone.weather.api.config.WebConfiguration;
import org.project.capstone.weather.api.interceptor.UnitsContext;
import org.project.capstone.weather.api.interceptor.UnitsRequestParameterInterceptor;
import org.project.capstone.weather.api.security.JwtFilter;
import org.project.capstone.weather.api.security.JwtService;
import org.project.capstone.weather.api.security.TokenBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(
        {
                UnitsContext.class,
                UnitsRequestParameterInterceptor.class,
                WebConfiguration.class,
                JwtFilter.class,
                JwtService.class,
                TokenBlackListService.class,
                UserDetailsService.class
        }
)
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

}
