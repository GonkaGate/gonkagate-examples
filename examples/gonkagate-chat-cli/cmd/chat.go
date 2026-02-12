package cmd

import (
	"fmt"

	"gonkagate-chat-cli/internal/chat"
	"gonkagate-chat-cli/internal/config"

	"github.com/spf13/cobra"
)

func newChatCommand() *cobra.Command {
	var streamValue string
	var modelOverride string
	var systemPrompt string
	var savePath string
	var temperature float64
	var smoke bool

	command := &cobra.Command{
		Use:          "chat",
		Short:        "Start interactive chat",
		SilenceUsage: true,
		RunE: func(cmd *cobra.Command, args []string) error {
			cfg, err := config.Load(config.Options{
				StreamValue:  streamValue,
				Model:        modelOverride,
				SystemPrompt: systemPrompt,
				SavePath:     savePath,
				Temperature:  temperature,
				Smoke:        smoke,
			})
			if err != nil {
				return err
			}

			if cfg.Smoke {
				_, err := fmt.Fprintln(cmd.OutOrStdout(), "Smoke check passed: config is valid and command can start.")
				return err
			}

			return chat.Run(cmd.Context(), cfg, cmd.InOrStdin(), cmd.OutOrStdout(), cmd.ErrOrStderr())
		},
	}

	command.Flags().StringVar(&streamValue, "stream", "true", "Enable streaming output: true or false.")
	command.Flags().StringVar(&modelOverride, "model", "", "Model ID override. Uses GONKAGATE_MODEL when empty.")
	command.Flags().StringVar(&systemPrompt, "system", "", "System prompt override.")
	command.Flags().StringVar(&savePath, "save", "", "Auto-save conversation history on exit to this path.")
	command.Flags().Float64Var(&temperature, "temperature", 0.2, "Temperature between 0 and 2.")
	command.Flags().BoolVar(&smoke, "smoke", false, "Validate startup and configuration without network requests.")

	return command
}
