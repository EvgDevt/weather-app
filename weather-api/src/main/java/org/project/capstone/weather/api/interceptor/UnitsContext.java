package org.project.capstone.weather.api.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Setter
@Component
@RequestScope
public class UnitsContext {

    private String units = "metric";
}
