package io.github.filipolszewski.cookbook.util;

import com.github.slugify.Slugify;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlugUtil {

    public static String slugify(String str) {
        return Slugify.builder().build().slugify(str);
    }
}
