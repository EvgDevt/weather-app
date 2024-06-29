package org.project.capstone.weather.api.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(protected * org.project.capstone.weather.api.handler.GlobalExceptionHandler.*(..))")
    public void exceptionHandlerMethods() {
    }

    @AfterReturning(
            value = "exceptionHandlerMethods()",
            returning = "result",
            argNames = "result")
    public void addLoggingAfterThrowing(Object result) {
        log.warn("Exception handled by GlobalExceptionHandler: {}", result);
    }
}
