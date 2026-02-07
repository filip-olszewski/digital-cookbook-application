package io.github.filipolszewski.cookbook.util;

import java.util.Objects;

public class UpdateUtil {
    public static boolean isChanged(String newValue, String oldValue) {
        return !Objects.equals(newValue, oldValue) && !newValue.isBlank();
    }
}
