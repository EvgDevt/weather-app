package org.project.capstone.weather.api.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.capstone.weather.api.excpetion.LocationNotFoundException;
import org.project.capstone.weather.api.excpetion.UserAlreadyExistsException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;


    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Locale locale = request.getLocale();
        ProblemDetail problemDetail = createProblemDetail(BAD_REQUEST, "errors.400.title", locale);

        List<String> errors = ex.getFieldErrors().stream().map(ObjectError::getDefaultMessage).toList();

        problemDetail.setProperty("error", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String messageKey, Locale locale) {
        return ProblemDetail.forStatusAndDetail(status, messageSource.getMessage(messageKey, new Object[0], "Error", locale));
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<Object> handleResponseStatusException(Locale locale) {
        ProblemDetail problemDetail = createProblemDetail(NOT_FOUND, "errors.404.title", locale);
        problemDetail.setProperty("error", messageSource.getMessage("error.404.no_data.description", new Object[0], locale));

        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    protected ResponseEntity<ExceptionResponse> handleLocationNotFoundException(Locale locale) {
        String message = messageSource.getMessage("errors.404.location.description", new Object[0], "errors.404.title", locale);
        ExceptionResponse response = ExceptionResponse.builder().code(404).errorDescription(message).build();

        return ResponseEntity.status(NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(WebRequest request) {
        Locale locale = request.getLocale();
        ProblemDetail problemDetail = createProblemDetail(BAD_REQUEST, "errors.400.title", locale);
        problemDetail.setProperty("error", messageSource.getMessage("errors.400.user_already_exists", new Object[0], locale));

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ExceptionResponse> handleBadCredentialsException(BadCredentialsException ex, Locale locale) {

        return buildLocalizedExceptionResponse("errors.401.title", UNAUTHORIZED, "errors.401.title", ex, locale);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {

        return buildLocalizedExceptionResponse("errors.403.denied", FORBIDDEN, "errors.403.denied", ex, locale);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, Locale locale) {

        return buildLocalizedExceptionResponse("errors.unique.violation", BAD_REQUEST, "", ex, locale);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception ex, Locale locale) {
        log.error("Unexpected error occurred: {}", ex.getLocalizedMessage());
        return buildLocalizedExceptionResponse("errors.500.internal", INTERNAL_SERVER_ERROR, "errors.500.internal",  ex, locale);
    }

    private ResponseEntity<ExceptionResponse> buildLocalizedExceptionResponse(String code,
                                                                              HttpStatusCode statusCode,
                                                                              String descriptionCode,
                                                                              Throwable throwable,
                                                                              Locale locale) {
        String defaultMessage = throwable.getMessage();
        String message = messageSource.getMessage(code, new Object[0], defaultMessage, locale);
        String errDescr = messageSource.getMessage(descriptionCode, new Object[0], defaultMessage, locale);

        return ResponseEntity.status(statusCode)
                .body(ExceptionResponse.builder()
                        .code(statusCode.value())
                        .errorDescription(errDescr)
                        .error(message)
                        .build());
    }
}
