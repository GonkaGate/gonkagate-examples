package com.gonkagate.examples;

public final class Main {
    private static final String SYSTEM_PROMPT = "You are a concise assistant.";
    private static final String USER_PROMPT = "Say hi from GonkaGate in one short sentence.";

    private Main() {
    }

    public static void main(String[] args) {
        AppConfig config = ConfigLoader.load();

        if (CliArgs.hasFlag(args, "--smoke")) {
            System.out.println("Smoke check passed: script can start and parse configuration.");
            return;
        }

        if (Strings.isBlank(config.apiKey())) {
            ErrorHandler.fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (Strings.isBlank(config.model())) {
            ErrorHandler.fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        try {
            String text = ChatService.complete(config, SYSTEM_PROMPT, USER_PROMPT);

            System.out.println("Model response:");
            System.out.println(text);
        } catch (Exception error) {
            ErrorHandler.handleRequestError(error);
        }
    }
}
