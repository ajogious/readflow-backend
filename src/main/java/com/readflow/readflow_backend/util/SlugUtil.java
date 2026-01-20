package com.readflow.readflow_backend.util;

import java.util.Locale;

public class SlugUtil {
    private SlugUtil() {
    }

    public static String toSlug(String input) {
        String s = input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("(^-+|-+$)", "");
        return s.isBlank() ? "content" : s;
    }
}
