package br.com.solutis.squad1.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    private static List<ErrorType> getErrors(List<FieldError> fieldErrors) {
        return fieldErrors
                .stream()
                .map(fieldError -> new ErrorType(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleForbiddenException(UnauthorizedException ex) {
        // TODO: Retornar erro de autenticação quando usuario não passar o token jwt
        LOGGER.error("Unauthorized", ex);
        return new ExceptionResponse(
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                HttpStatus.UNAUTHORIZED.value(),
                List.of(new ErrorType("body", ex.getMessage())),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception ex) {
        LOGGER.error("Internal server error", ex);
        return new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of(new ErrorType("body", ex.getLocalizedMessage())),
                LocalDateTime.now()
        );
    }

    public record ErrorType(String field, String message) {
    }

    public record ExceptionResponse(String message, int status, List<ErrorType> errors, LocalDateTime timestamp) {
    }
}
