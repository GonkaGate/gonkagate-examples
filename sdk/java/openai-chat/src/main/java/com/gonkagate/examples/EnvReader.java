package com.gonkagate.examples;

import io.github.cdimascio.dotenv.Dotenv;

final class EnvReader {
    private final Dotenv dotenv;

    private EnvReader(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    static EnvReader create() {
        return new EnvReader(loadDotenv());
    }

    String get(String key) {
        if (dotenv != null) {
            String dotenvValue = Strings.trimToNull(dotenv.get(key));
            if (dotenvValue != null) {
                return dotenvValue;
            }
        }

        return Strings.trimToNull(System.getenv(key));
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
}
