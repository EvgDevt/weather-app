package org.project.capstone.weather.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.capstone.weather.api.controller.annotation.TemperatureConvertable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UnitsRequestParameterInterceptor implements HandlerInterceptor {

    private final UnitsContext unitsContext;


    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getBeanType().isAnnotationPresent(TemperatureConvertable.class)) {
                String units = request.getParameter("units");
                if (units != null) {
                    unitsContext.setUnits(units);
                }
            }
        }
        return true;
    }
}
