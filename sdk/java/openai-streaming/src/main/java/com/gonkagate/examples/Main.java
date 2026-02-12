package com.gonkagate.examples;

public final class Main {
    private static final String DEFAULT_PROMPT = "Say hi from GonkaGate in one short sentence.";
    private static final ConfigLoader CONFIG_LOADER = new ConfigLoader();
    private static final RequestErrorHandler ERROR_HANDLER = new RequestErrorHandler();
    private static final StreamingChatRunner STREAMING_CHAT_RUNNER = new StreamingChatRunner();

    private Main() {
    }

    public static void main(String[] args) {
        AppConfig config = CONFIG_LOADER.load();

        if (CliArgs.hasSmokeFlag(args)) {
            System.out.println("Smoke check passed: config is valid and script can start.");
            return;
        }

        if (isBlank(config.apiKey())) {
            fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (isBlank(config.model()) || AppConfig.DEFAULT_MODEL.equals(config.model())) {
            fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        String prompt = CliArgs.parsePrompt(args, DEFAULT_PROMPT);

        try {
            STREAMING_CHAT_RUNNER.run(config, prompt);
        } catch (Exception error) {
            ERROR_HANDLER.handle(error);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void fail(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
    }
}
