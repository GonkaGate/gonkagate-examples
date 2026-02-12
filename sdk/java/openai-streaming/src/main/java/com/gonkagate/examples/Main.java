package com.gonkagate.examples;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.stream.Stream;

public final class Main {
    private static final String DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";
    private static final String DEFAULT_MODEL = "your_model";
    private static final String DEFAULT_PROMPT = "Say hi from GonkaGate in one short sentence.";
    private static final Dotenv DOTENV = loadDotenv();

    private Main() {
    }

    public static void main(String[] args) {
        Config config = loadConfig();

        if (hasFlag(args, "--smoke")) {
            System.out.println("Smoke check passed: config is valid and script can start.");
            return;
        }

        if (isBlank(config.apiKey())) {
            fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (isBlank(config.model()) || DEFAULT_MODEL.equals(config.model())) {
            fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        String prompt = parsePrompt(args);

        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(config.apiKey())
            .baseUrl(config.baseUrl())
            .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(config.model())
            .addSystemMessage("You are a concise assistant.")
            .addUserMessage(prompt)
            .temperature(0.2)
            .build();

        try (
            StreamResponse<ChatCompletionChunk> streamResponse = client.chat().completions().createStreaming(request);
            Stream<ChatCompletionChunk> chunks = streamResponse.stream()
        ) {
            boolean emittedContent = false;
            var iterator = chunks.iterator();

            while (iterator.hasNext()) {
                ChatCompletionChunk chunk = iterator.next();
                String token = extractToken(chunk);
                if (token.isEmpty()) {
                    continue;
                }

                System.out.print(token);
                emittedContent = true;
            }

            if (emittedContent) {
                System.out.println();
            }

            System.out.println("[stream complete]");
        } catch (Exception error) {
            handleRequestError(error);
        }
    }

    private static String extractToken(ChatCompletionChunk chunk) {
        List<ChatCompletionChunk.Choice> choices = chunk.choices();
        if (choices.isEmpty()) {
            return "";
        }

        return choices.get(0).delta().content().orElse("");
    }

    private static String parsePrompt(String[] args) {
        StringBuilder promptBuilder = new StringBuilder();

        for (String arg : args) {
            if ("--smoke".equalsIgnoreCase(arg)) {
                continue;
            }

            if (promptBuilder.length() > 0) {
                promptBuilder.append(' ');
            }

            promptBuilder.append(arg);
        }

        String prompt = promptBuilder.toString().trim();
        return prompt.isEmpty() ? DEFAULT_PROMPT : prompt;
    }

    private static Config loadConfig() {
        warnIfBaseUrlOverrideSet();

        String apiKey = firstNonBlank(
            getEnv("GONKAGATE_API_KEY"),
            getEnv("OPENAI_API_KEY")
        );

        String baseUrl = DEFAULT_BASE_URL;

        String model = firstNonBlank(
            getEnv("GONKAGATE_MODEL"),
            DEFAULT_MODEL
        );

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
            if (current instanceof OpenAIServiceException serviceException) {
                return serviceException.statusCode();
            }

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
