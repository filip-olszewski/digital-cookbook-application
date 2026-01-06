package io.github.filipolszewski.cookbook.exception;

import io.github.filipolszewski.cookbook.util.ProblemDetailBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.NOT_FOUND)
                .title("Resource not found")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.CONFLICT)
                .title("Resource already exists")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail handleResourceConflictException(ResourceConflictException ex) {
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.CONFLICT)
                .title("Resource Conflict")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.FORBIDDEN)
                .title("Access Denied")
                .message("You do not have permission to perform this action.")
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .title("Authentication Failed")
                .message("Invalid email or password")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
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
        return ProblemDetailBuilder.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("Malformed Request")
                .message("The request body is missing or contains invalid JSON.")
                .build();
    }
}