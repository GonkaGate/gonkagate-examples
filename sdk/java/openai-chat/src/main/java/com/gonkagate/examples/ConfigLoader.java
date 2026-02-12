package com.gonkagate.examples;

final class ConfigLoader {
    private static final String DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";
    private static final EnvReader ENV_READER = EnvReader.create();

    private ConfigLoader() {
    }

    static AppConfig load() {
        warnIfBaseUrlOverrideSet();

        String apiKey = Strings.firstNonBlank(
            ENV_READER.get("GONKAGATE_API_KEY"),
            ENV_READER.get("OPENAI_API_KEY")
        );

        String baseUrl = DEFAULT_BASE_URL;
        String model = ENV_READER.get("GONKAGATE_MODEL");

        return new AppConfig(apiKey, baseUrl, model);
    }

    private static void warnIfBaseUrlOverrideSet() {
        if (Strings.isBlank(ENV_READER.get("GONKAGATE_BASE_URL")) && Strings.isBlank(ENV_READER.get("OPENAI_BASE_URL"))) {
            return;
        }

        System.err.println(
            "Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to "
                + DEFAULT_BASE_URL
                + "."
        );
    }
}
