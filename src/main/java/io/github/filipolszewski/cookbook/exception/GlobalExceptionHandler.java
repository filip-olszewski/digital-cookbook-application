package io.github.filipolszewski.cookbook.exception;

import io.github.filipolszewski.cookbook.util.ProblemDetailBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j // Added Lombok logger
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.NOT_FOUND)
                .title("Resource not found")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.warn("Resource already exists: {}", ex.getMessage());
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.CONFLICT)
                .title("Resource already exists")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail handleResourceConflictException(ResourceConflictException ex) {
        log.warn("Resource conflict: {}", ex.getMessage());
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.CONFLICT)
                .title("Resource Conflict")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied exception triggered: {}", ex.getMessage());
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.FORBIDDEN)
                .title("Access Denied")
                .message("You do not have permission to perform this action.")
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        log.warn("Failed authentication attempt: Bad credentials provided.");
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .title("Authentication Failed")
                .message("Invalid email or password")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for incoming request");
        ProblemDetail problem = ProblemDetailBuilder.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("Invalid Request Content")
                .message("Validation failed")
                .build();

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed request body received: {}", ex.getMessage());
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("Malformed Request")
                .message("The request body is missing or contains invalid JSON.")
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherExceptions(Exception ex) {
        log.error("An unexpected error occurred in the application", ex);
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .build();
    }
}