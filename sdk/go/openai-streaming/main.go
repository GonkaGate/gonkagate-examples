package main

import (
	"context"
	"errors"
	"flag"
	"fmt"
	"os"
	"strings"

	"github.com/joho/godotenv"
	openai "github.com/openai/openai-go/v3"
	"github.com/openai/openai-go/v3/option"
)

const (
	defaultBaseURL = "https://api.gonkagate.com/v1"
	defaultModel   = "your_model"
	defaultPrompt  = "Say hi from GonkaGate in one short sentence."
)

type config struct {
	apiKey  string
	baseURL string
	model   string
}

func main() {
	_ = godotenv.Load()

	smokeMode, prompt := parseArgs()
	cfg := loadConfig()

	if smokeMode {
		fmt.Println("Smoke check passed: config is valid and script can start.")
		return
	}

	if cfg.apiKey == "" {
		fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.")
	}

	if cfg.model == "" || cfg.model == defaultModel {
		fail("Missing model. Set GONKAGATE_MODEL in your environment.")
	}

	client := openai.NewClient(
		option.WithAPIKey(cfg.apiKey),
		option.WithBaseURL(cfg.baseURL),
	)

	stream := client.Chat.Completions.NewStreaming(context.Background(), openai.ChatCompletionNewParams{
		Model: openai.ChatModel(cfg.model),
		Messages: []openai.ChatCompletionMessageParamUnion{
			openai.SystemMessage("You are a concise assistant."),
			openai.UserMessage(prompt),
		},
		Temperature: openai.Float(0.2),
	})

	emittedContent := false

	for stream.Next() {
		chunk := stream.Current()
		if len(chunk.Choices) == 0 {
			continue
		}

		token := chunk.Choices[0].Delta.Content
		if token == "" {
			continue
		}

		fmt.Print(token)
		emittedContent = true
	}

	if err := stream.Err(); err != nil {
		handleError(err)
	}

	if emittedContent {
		fmt.Println()
	}

	fmt.Println("[stream complete]")
}

func parseArgs() (bool, string) {
	smokeMode := flag.Bool("smoke", false, "Run startup validation without making a network request.")
	flag.Parse()

	prompt := strings.TrimSpace(strings.Join(flag.Args(), " "))
	if prompt == "" {
		prompt = defaultPrompt
	}

	return *smokeMode, prompt
}

func loadConfig() config {
	warnIfBaseURLOverrideSet()

	return config{
		apiKey:  firstNonEmpty(os.Getenv("GONKAGATE_API_KEY"), os.Getenv("OPENAI_API_KEY")),
		baseURL: defaultBaseURL,
		model:   firstNonEmpty(os.Getenv("GONKAGATE_MODEL"), defaultModel),
	}
}

func warnIfBaseURLOverrideSet() {
	if strings.TrimSpace(os.Getenv("GONKAGATE_BASE_URL")) == "" &&
		strings.TrimSpace(os.Getenv("OPENAI_BASE_URL")) == "" {
		return
	}

	fmt.Fprintf(
		os.Stderr,
		"Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to %s.\n",
		defaultBaseURL,
	)
}

func firstNonEmpty(values ...string) string {
	for _, value := range values {
		trimmed := strings.TrimSpace(value)
		if trimmed != "" {
			return trimmed
		}
	}
	return ""
}

func handleError(err error) {
	var apiErr *openai.Error
	if errors.As(err, &apiErr) {
		switch apiErr.StatusCode {
		case 401:
			fail("401 Unauthorized. Check your API key.")
		case 402:
			fail("402 Payment Required. Check your GonkaGate balance or billing status.")
		case 429:
			fail("429 Too Many Requests. Slow down request rate and retry.")
		case 503:
			fail("503 Service Unavailable. Retry in a few seconds.")
		}
	}

	message := strings.TrimSpace(err.Error())
	if message == "" {
		message = "Unknown request error."
	}
	fail(message)
}

func fail(message string) {
	fmt.Fprintf(os.Stderr, "Error: %s\n", message)
	os.Exit(1)
}
