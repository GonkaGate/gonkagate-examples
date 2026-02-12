package com.gonkagate.examples;

import com.openai.errors.OpenAIServiceException;

public final class RequestErrorHandler {
    public void handle(Exception error) {
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

    private Integer findStatusCode(Throwable error) {
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

    private Integer readStatusCode(Object source) {
        Integer statusCode = readIntProperty(source, "statusCode");
        if (statusCode != null) {
            return statusCode;
        }

        return readIntProperty(source, "status");
    }

    private Integer readIntProperty(Object source, String propertyName) {
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

    private Object readProperty(Object source, String propertyName) {
        if (source == null) {
            return null;
        }

        try {
            return source.getClass().getMethod(propertyName).invoke(source);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }

        return "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void fail(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
    }
}
