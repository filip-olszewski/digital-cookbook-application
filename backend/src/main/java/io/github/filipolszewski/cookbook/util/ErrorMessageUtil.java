package io.github.filipolszewski.cookbook.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessageUtil {

    public static String notFound(Class<?> resource, String field, Object value) {
        return String.format(
                "%s with %s ['%s'] not found.",
                resource.getSimpleName(),
                field,
                value
            );
    }

    public static String exists(Class<?> resource, String field, Object value) {
        return String.format(
                "%s with this %s ['%s'] already exists.",
                resource.getSimpleName(),
                field,
                value
        );
    }
}
