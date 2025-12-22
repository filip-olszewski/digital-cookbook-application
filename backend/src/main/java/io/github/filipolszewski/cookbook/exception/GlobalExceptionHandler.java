package io.github.filipolszewski.cookbook.exception;

import io.github.filipolszewski.cookbook.util.ProblemDetailBuilder;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetailBuilder.builder()
                        .message(ex.getMessage())
                        .title("Resource not found")
                        .status(HttpStatus.NOT_FOUND)
                        .build()
                );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetailBuilder.builder()
                        .message(ex.getMessage())
                        .title("Resource already exists")
                        .status(HttpStatus.CONFLICT)
                        .build()
                );
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ProblemDetail> handleResourceConflictException(ResourceConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetailBuilder.builder()
                        .message(ex.getMessage())
                        .title("Resource Conflict")
                        .status(HttpStatus.CONFLICT)
                        .build()
                );
    }

}
