package chat

import (
	"fmt"
	"strings"
)

const (
	commandHelp   = "help"
	commandModel  = "model"
	commandStream = "stream"
	commandReset  = "reset"
	commandSave   = "save"
	commandExit   = "exit"
)

type SlashCommand struct {
	Name string
	Arg  string
}

func ParseSlashCommand(input string) (SlashCommand, error) {
	trimmed := strings.TrimSpace(input)
	if !strings.HasPrefix(trimmed, "/") {
		return SlashCommand{}, fmt.Errorf("not a slash command: %q", input)
	}

	fields := strings.Fields(trimmed)
	if len(fields) == 0 {
		return SlashCommand{}, fmt.Errorf("empty command")
	}

	name := strings.TrimPrefix(strings.ToLower(fields[0]), "/")
	switch name {
	case commandHelp, commandReset, commandExit:
		if len(fields) != 1 {
			return SlashCommand{}, fmt.Errorf("/%s does not accept arguments", name)
		}
		return SlashCommand{Name: name}, nil

	case commandModel:
		if len(fields) < 2 {
			return SlashCommand{}, fmt.Errorf("usage: /model <model-id>")
		}
		return SlashCommand{Name: name, Arg: strings.Join(fields[1:], " ")}, nil

	case commandStream:
		if len(fields) != 2 {
			return SlashCommand{}, fmt.Errorf("usage: /stream on|off")
		}

		value := strings.ToLower(fields[1])
		if value != "on" && value != "off" {
			return SlashCommand{}, fmt.Errorf("usage: /stream on|off")
		}
		return SlashCommand{Name: name, Arg: value}, nil

	case commandSave:
		if len(fields) == 1 {
			return SlashCommand{Name: name}, nil
		}
		return SlashCommand{Name: name, Arg: strings.Join(fields[1:], " ")}, nil
	}

	return SlashCommand{}, fmt.Errorf("unknown command %q. Use /help", fields[0])
}

func HelpText() string {
	return strings.Join([]string{
		"Commands:",
		"  /help                Show available commands",
		"  /model <model-id>    Change model for next requests",
		"  /stream on|off       Toggle streaming mode",
		"  /reset               Reset conversation history (keeps system prompt)",
		"  /save [path]         Save transcript to JSON",
		"  /exit                Exit chat",
	}, "\n")
}
