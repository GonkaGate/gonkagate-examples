package com.gonkagate.examples;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

public final class Main {
    private static final String DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";
    private static final Dotenv DOTENV = loadDotenv();

    private Main() {
    }

    public static void main(String[] args) {
        Config config = loadConfig();

        if (hasFlag(args, "--smoke")) {
            System.out.println("Smoke check passed: script can start and parse configuration.");
            return;
        }

        if (isBlank(config.apiKey())) {
            fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (isBlank(config.model())) {
            fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(config.apiKey())
            .baseUrl(config.baseUrl())
            .build();

        try {
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(config.model())
                .addSystemMessage("You are a concise assistant.")
                .addUserMessage("Say hi from GonkaGate in one short sentence.")
                .build();

            ChatCompletion response = client.chat().completions().create(request);
            String text = extractText(response);

            if (isBlank(text)) {
                fail("Received a response but no message content in choices[0].");
            }

            System.out.println("Model response:");
            System.out.println(text);
        } catch (Exception error) {
            handleRequestError(error);
        }
    }

    private static String extractText(ChatCompletion response) {
        return response.choices().stream()
            .findFirst()
            .flatMap(choice -> choice.message().content())
            .map(String::trim)
            .orElse("");
    }

    private static Config loadConfig() {
        warnIfBaseUrlOverrideSet();

        String apiKey = firstNonBlank(
            getEnv("GONKAGATE_API_KEY"),
            getEnv("OPENAI_API_KEY")
        );

        String baseUrl = DEFAULT_BASE_URL;

        String model = getEnv("GONKAGATE_MODEL");

        return new Config(apiKey, baseUrl, model);
    }

    private static void warnIfBaseUrlOverrideSet() {
        if (isBlank(getEnv("GONKAGATE_BASE_URL")) && isBlank(getEnv("OPENAI_BASE_URL"))) {
            return;
        }

        System.err.println(
            "Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to "
                + DEFAULT_BASE_URL
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

    private static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }

    private static void handleRequestError(Exception error) {
        Integer statusCode = findStatusCode(error);

        if (statusCode != null) {
            switch (statusCode) {
                case 401 -> fail("401 Unauthorized. Check your API key.");
                case 402 -> fail("402 Payment Required. Check your GonkaGate balance or billing status.");
                case 429 -> fail("429 Too Many Requests. Slow down request rate and retry.");
                case 503 -> fail("503 Service Unavailable. Retry in a few seconds.");
                default -> {
                    // Fall through to generic error message below.
                }
            }
        }

        String message = firstNonBlank(error.getMessage(), "Unknown request error.");
        fail(message);
    }

    private static Integer findStatusCode(Throwable error) {
        for (Throwable current = error; current != null; current = current.getCause()) {
            Integer direct = readStatusCode(current);
            if (direct != null) {
                return direct;
            }

            Object response = readProperty(current, "response");
            Integer responseCode = readStatusCode(response);
            if (responseCode != null) {
                return responseCode;
            }
        }

        return null;
    }

    private static Integer readStatusCode(Object source) {
        Integer statusCode = readIntProperty(source, "statusCode");
        if (statusCode != null) {
            return statusCode;
        }

        return readIntProperty(source, "status");
    }

    private static Integer readIntProperty(Object source, String propertyName) {
        if (source == null) {
            return null;
        }

        try {
            Object value = source.getClass().getMethod(propertyName).invoke(source);
            if (value instanceof Number number) {
                return number.intValue();
            }

            if (value != null) {
                Object intValue = value.getClass().getMethod("value").invoke(value);
                if (intValue instanceof Number number) {
                    return number.intValue();
                }
            }
        } catch (Exception ignored) {
            // Ignore missing methods and parse failures.
        }

        return null;
    }

    private static Object readProperty(Object source, String propertyName) {
        if (source == null) {
            return null;
        }

        try {
            return source.getClass().getMethod(propertyName).invoke(source);
        } catch (Exception ignored) {
            return null;
        }
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

    private static void fail(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
    }

    private record Config(String apiKey, String baseUrl, String model) {
    }
}
