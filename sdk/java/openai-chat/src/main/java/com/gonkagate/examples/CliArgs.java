package com.gonkagate.examples;

final class CliArgs {
    private CliArgs() {
    }

    static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }
}
