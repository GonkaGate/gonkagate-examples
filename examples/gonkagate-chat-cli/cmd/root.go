package cmd

import "github.com/spf13/cobra"

var rootCmd = &cobra.Command{
	Use:           "gonkagate-chat",
	Short:         "Cross-platform interactive CLI chat for GonkaGate",
	SilenceErrors: true,
	Long: "gonkagate-chat is a production-style interactive CLI example that talks " +
		"to GonkaGate through the OpenAI-compatible Chat Completions API.",
}

func Execute() error {
	return rootCmd.Execute()
}

func init() {
	rootCmd.AddCommand(newChatCommand())
}
