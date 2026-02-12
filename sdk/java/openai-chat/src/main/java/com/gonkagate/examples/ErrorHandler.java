package com.gonkagate.examples;

final class ErrorHandler {
    private ErrorHandler() {
    }

    static void handleRequestError(Exception error) {
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

        String message = Strings.firstNonBlank(error.getMessage(), "Unknown request error.");
        fail(message);
    }

    static void fail(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
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
}
