package com.gonkagate.examples;

public record AppConfig(String apiKey, String baseUrl, String model) {
    public static final String DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";
    public static final String DEFAULT_MODEL = "your_model";
}
