package main

import (
	"context"
	"errors"
	"fmt"
	"os"
	"strings"

	"github.com/joho/godotenv"
	openai "github.com/openai/openai-go/v3"
	"github.com/openai/openai-go/v3/option"
)

const defaultBaseURL = "https://api.gonkagate.com/v1"

type config struct {
	apiKey  string
	baseURL string
	model   string
}

func main() {
	_ = godotenv.Load()

	cfg := loadConfig()
	if hasFlag("--smoke") {
		fmt.Println("Smoke check passed: script can start and parse configuration.")
		return
	}

	if cfg.apiKey == "" {
		fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.")
	}

	if cfg.model == "" {
		fail("Missing model. Set GONKAGATE_MODEL in your environment.")
	}

	client := openai.NewClient(
		option.WithAPIKey(cfg.apiKey),
		option.WithBaseURL(cfg.baseURL),
	)

	response, err := client.Chat.Completions.New(context.Background(), openai.ChatCompletionNewParams{
		Model: openai.ChatModel(cfg.model),
		Messages: []openai.ChatCompletionMessageParamUnion{
			openai.SystemMessage("You are a concise assistant."),
			openai.UserMessage("Say hi from GonkaGate in one short sentence."),
		},
	})
	if err != nil {
		handleRequestError(err)
	}

	if len(response.Choices) == 0 {
		fail("Received a response but no choices in the payload.")
	}

	text := strings.TrimSpace(response.Choices[0].Message.Content)
	if text == "" {
		fail("Received a response but no message content in choices[0].")
	}

	fmt.Println("Model response:")
	fmt.Println(text)
}

func loadConfig() config {
	warnIfBaseURLOverrideSet()

	apiKey := getFirstNonEmptyEnv("GONKAGATE_API_KEY", "OPENAI_API_KEY")

	return config{
		apiKey:  apiKey,
		baseURL: defaultBaseURL,
		model:   strings.TrimSpace(os.Getenv("GONKAGATE_MODEL")),
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

func getFirstNonEmptyEnv(keys ...string) string {
	for _, key := range keys {
		value := strings.TrimSpace(os.Getenv(key))
		if value != "" {
			return value
		}
	}
	return ""
}

func hasFlag(target string) bool {
	for _, arg := range os.Args[1:] {
		if arg == target {
			return true
		}
	}
	return false
}

func handleRequestError(err error) {
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
