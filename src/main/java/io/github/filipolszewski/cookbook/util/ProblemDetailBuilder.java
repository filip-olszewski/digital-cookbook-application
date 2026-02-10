package io.github.filipolszewski.cookbook.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProblemDetailBuilder {
    private ProblemDetail problemDetail;

    public static ProblemDetailBuilder builder() {
        var builder = new ProblemDetailBuilder();
        builder.problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return builder;
    }

    public ProblemDetailBuilder message(String message) {
        problemDetail.setDetail(message);
        return this;
    }

    public ProblemDetailBuilder title(String title) {
        problemDetail.setTitle(title);
        return this;
    }

    public ProblemDetailBuilder status(HttpStatus status) {
        problemDetail.setStatus(status);
        return this;
    }

    public ProblemDetail build() {
        return problemDetail;
    }
}
