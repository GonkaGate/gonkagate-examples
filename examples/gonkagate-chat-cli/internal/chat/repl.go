package chat

import (
	"bufio"
	"context"
	"errors"
	"fmt"
	"io"
	"os"
	"os/signal"
	"strings"

	"gonkagate-chat-cli/internal/config"
	apperrors "gonkagate-chat-cli/internal/errors"
	"gonkagate-chat-cli/internal/output"
	"gonkagate-chat-cli/internal/storage"
)

const defaultSavePath = "gonkagate-chat-history.json"

type runtimeState struct {
	model    string
	stream   bool
	savePath string
	autoSave bool
}

func Run(ctx context.Context, cfg config.Config, stdin io.Reader, stdout io.Writer, stderr io.Writer) error {
	renderer := output.NewRenderer(stdout, stderr)
	client := NewClient(cfg.APIKey, cfg.BaseURL)
	session := NewSession(cfg.SystemPrompt)

	state := runtimeState{
		model:    cfg.Model,
		stream:   cfg.Stream,
		savePath: cfg.SavePath,
		autoSave: strings.TrimSpace(cfg.SavePath) != "",
	}

	renderer.Info("Interactive chat started. Type /help for commands.")

	scanner := bufio.NewScanner(stdin)
	scanner.Buffer(make([]byte, 0, 64*1024), 1024*1024)

	for {
		renderer.Prompt()
		if !scanner.Scan() {
			if err := scanner.Err(); err != nil {
				return fmt.Errorf("read input: %w", err)
			}
			renderer.Info("Input closed. Exiting chat.")
			break
		}

		input := strings.TrimSpace(scanner.Text())
		if input == "" {
			continue
		}

		if strings.HasPrefix(input, "/") {
			exit, err := handleSlashCommand(input, cfg, &state, session, renderer)
			if err != nil {
				renderer.Error(err.Error())
				continue
			}
			if exit {
				break
			}
			continue
		}

		session.AddUser(input)
		renderer.AssistantPrefix()

		answer, err := runCompletionWithInterrupt(ctx, client, session.Messages(), RequestOptions{
			Model:       state.model,
			Temperature: cfg.Temperature,
			Stream:      state.stream,
		}, renderer)
		if err != nil {
			session.RemoveLastUserMessage()
			renderer.Newline()
			renderer.Error(apperrors.FriendlyError(err))
			continue
		}

		if state.stream {
			renderer.Newline()
		} else {
			renderer.Plain(answer)
		}

		session.AddAssistant(answer)
	}

	if state.autoSave && state.savePath != "" {
		if err := persistSession(state.savePath, cfg, state, session); err != nil {
			return err
		}
		renderer.Info(fmt.Sprintf("History saved to %s", state.savePath))
	}

	return nil
}

func handleSlashCommand(
	input string,
	cfg config.Config,
	state *runtimeState,
	session *Session,
	renderer *output.Renderer,
) (bool, error) {
	command, err := ParseSlashCommand(input)
	if err != nil {
		return false, err
	}

	switch command.Name {
	case commandHelp:
		renderer.Plain(HelpText())
		return false, nil

	case commandModel:
		state.model = strings.TrimSpace(command.Arg)
		renderer.Info(fmt.Sprintf("Model set to %s", state.model))
		return false, nil

	case commandStream:
		state.stream = command.Arg == "on"
		renderer.Info(fmt.Sprintf("Streaming set to %t", state.stream))
		return false, nil

	case commandReset:
		session.Reset()
		renderer.Info("Conversation reset.")
		return false, nil

	case commandSave:
		savePath := strings.TrimSpace(command.Arg)
		if savePath == "" {
			if state.savePath != "" {
				savePath = state.savePath
			} else {
				savePath = defaultSavePath
			}
		}

		if err := persistSession(savePath, cfg, *state, session); err != nil {
			return false, err
		}

		state.savePath = savePath
		renderer.Info(fmt.Sprintf("History saved to %s", savePath))
		return false, nil

	case commandExit:
		renderer.Info("Exiting chat.")
		return true, nil
	}

	return false, fmt.Errorf("unknown command %q", command.Name)
}

func runCompletionWithInterrupt(
	parent context.Context,
	client *Client,
	messages []Message,
	opts RequestOptions,
	renderer *output.Renderer,
) (string, error) {
	requestCtx, cancel := context.WithCancel(parent)
	defer cancel()

	interrupts := make(chan os.Signal, 1)
	signal.Notify(interrupts, os.Interrupt)
	defer func() {
		signal.Stop(interrupts)
	}()

	interrupted := make(chan struct{}, 1)
	go func() {
		select {
		case <-interrupts:
			renderer.Info("Interrupt received. Canceling generation...")
			cancel()
			interrupted <- struct{}{}
		case <-requestCtx.Done():
		}
	}()

	var onToken func(string)
	if opts.Stream {
		onToken = renderer.Token
	}

	answer, err := client.Complete(requestCtx, messages, opts, onToken)
	if err == nil {
		return answer, nil
	}

	select {
	case <-interrupted:
		if errors.Is(err, context.Canceled) {
			return "", fmt.Errorf("generation canceled")
		}
		return "", fmt.Errorf("generation interrupted")
	default:
	}

	return "", err
}

func persistSession(path string, cfg config.Config, state runtimeState, session *Session) error {
	messages := session.Messages()
	serialized := make([]storage.Message, 0, len(messages))
	for _, message := range messages {
		serialized = append(serialized, storage.Message{
			Role:    message.Role,
			Content: message.Content,
		})
	}

	transcript := storage.Transcript{
		Meta: storage.Metadata{
			Model:        state.model,
			BaseURL:      cfg.BaseURL,
			Streaming:    state.stream,
			Temperature:  cfg.Temperature,
			SystemPrompt: session.SystemPrompt(),
		},
		Messages: serialized,
	}

	if err := storage.Save(path, transcript); err != nil {
		return fmt.Errorf("save history: %w", err)
	}

	return nil
}
