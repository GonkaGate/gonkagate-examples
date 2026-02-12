package config

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

const (
	defaultBaseURL      = "https://api.gonkagate.com/v1"
	defaultSystemPrompt = "You are a concise assistant."
)

type Config struct {
	APIKey       string
	BaseURL      string
	Model        string
	Stream       bool
	SystemPrompt string
	SavePath     string
	Temperature  float64
	Smoke        bool
}

type Options struct {
	StreamValue  string
	Model        string
	SystemPrompt string
	SavePath     string
	Temperature  float64
	Smoke        bool
}

func Load(opts Options) (Config, error) {
	stream, err := parseStreamValue(opts.StreamValue)
	if err != nil {
		return Config{}, err
	}

	if opts.Temperature < 0 || opts.Temperature > 2 {
		return Config{}, fmt.Errorf("invalid temperature %.2f: expected value between 0 and 2", opts.Temperature)
	}

	apiKey := firstNonEmpty(
		strings.TrimSpace(os.Getenv("GONKAGATE_API_KEY")),
		strings.TrimSpace(os.Getenv("OPENAI_API_KEY")),
	)
	if apiKey == "" {
		return Config{}, fmt.Errorf("missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY")
	}

	model := firstNonEmpty(
		strings.TrimSpace(opts.Model),
		strings.TrimSpace(os.Getenv("GONKAGATE_MODEL")),
	)
	if model == "" {
		return Config{}, fmt.Errorf("missing model. Set GONKAGATE_MODEL or pass --model")
	}

	warnIfBaseURLOverrideSet()

	systemPrompt := strings.TrimSpace(opts.SystemPrompt)
	if systemPrompt == "" {
		systemPrompt = defaultSystemPrompt
	}

	return Config{
		APIKey:       apiKey,
		BaseURL:      defaultBaseURL,
		Model:        model,
		Stream:       stream,
		SystemPrompt: systemPrompt,
		SavePath:     strings.TrimSpace(opts.SavePath),
		Temperature:  opts.Temperature,
		Smoke:        opts.Smoke,
	}, nil
}

func parseStreamValue(raw string) (bool, error) {
	value := strings.TrimSpace(raw)
	if value == "" {
		value = "true"
	}

	parsed, err := strconv.ParseBool(value)
	if err != nil {
		return false, fmt.Errorf("invalid --stream value %q: expected true or false", raw)
	}
	return parsed, nil
}

func warnIfBaseURLOverrideSet() {
	if strings.TrimSpace(os.Getenv("GONKAGATE_BASE_URL")) == "" &&
		strings.TrimSpace(os.Getenv("OPENAI_BASE_URL")) == "" {
		return
	}

	_, _ = fmt.Fprintln(
		os.Stderr,
		"Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to https://api.gonkagate.com/v1.",
	)
}

func firstNonEmpty(values ...string) string {
	for _, value := range values {
		if value != "" {
			return value
		}
	}
	return ""
}
