package com.gonkagate.examples;

final class Strings {
    private Strings() {
    }

    static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }

        return "";
    }

    static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
