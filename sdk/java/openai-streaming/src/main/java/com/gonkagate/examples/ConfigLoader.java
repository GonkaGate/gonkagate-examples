package com.gonkagate.examples;

import io.github.cdimascio.dotenv.Dotenv;

public final class ConfigLoader {
    private static final Dotenv DOTENV = loadDotenv();

    public AppConfig load() {
        warnIfBaseUrlOverrideSet();

        String apiKey = firstNonBlank(
            getEnv("GONKAGATE_API_KEY"),
            getEnv("OPENAI_API_KEY")
        );

        String baseUrl = AppConfig.DEFAULT_BASE_URL;

        String model = firstNonBlank(
            getEnv("GONKAGATE_MODEL"),
            AppConfig.DEFAULT_MODEL
        );

        return new AppConfig(apiKey, baseUrl, model);
    }

    private void warnIfBaseUrlOverrideSet() {
        if (isBlank(getEnv("GONKAGATE_BASE_URL")) && isBlank(getEnv("OPENAI_BASE_URL"))) {
            return;
        }

        System.err.println(
            "Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to "
                + AppConfig.DEFAULT_BASE_URL
                + "."
        );
    }

    private static Dotenv loadDotenv() {
        try {
            return Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getEnv(String key) {
        if (DOTENV != null) {
            String dotenvValue = trimToNull(DOTENV.get(key));
            if (dotenvValue != null) {
                return dotenvValue;
            }
        }

        return trimToNull(System.getenv(key));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }

        return "";
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
