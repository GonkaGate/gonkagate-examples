package com.gonkagate.examples;

public final class CliArgs {
    private static final String SMOKE_FLAG = "--smoke";

    private CliArgs() {
    }

    public static boolean hasSmokeFlag(String[] args) {
        for (String arg : args) {
            if (SMOKE_FLAG.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }

    public static String parsePrompt(String[] args, String defaultPrompt) {
        StringBuilder promptBuilder = new StringBuilder();

        for (String arg : args) {
            if (SMOKE_FLAG.equalsIgnoreCase(arg)) {
                continue;
            }

            if (promptBuilder.length() > 0) {
                promptBuilder.append(' ');
            }

            promptBuilder.append(arg);
        }

        String prompt = promptBuilder.toString().trim();
        return prompt.isEmpty() ? defaultPrompt : prompt;
    }
}
