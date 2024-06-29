package org.project.capstone.weather.api.config;

import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.interceptor.UnitsRequestParameterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final UnitsRequestParameterInterceptor interceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/weather-api/v1/weather-data/**");
    }
}
