package chat

import (
	"context"
	"errors"
	"strings"

	openai "github.com/openai/openai-go/v3"
	"github.com/openai/openai-go/v3/option"
)

type RequestOptions struct {
	Model       string
	Temperature float64
	Stream      bool
}

type Client struct {
	client openai.Client
}

func NewClient(apiKey string, baseURL string) *Client {
	return &Client{
		client: openai.NewClient(
			option.WithAPIKey(apiKey),
			option.WithBaseURL(baseURL),
		),
	}
}

func (c *Client) Complete(
	ctx context.Context,
	messages []Message,
	opts RequestOptions,
	onToken func(string),
) (string, error) {
	params := openai.ChatCompletionNewParams{
		Model:       openai.ChatModel(strings.TrimSpace(opts.Model)),
		Temperature: openai.Float(opts.Temperature),
		Messages:    toOpenAIMessages(messages),
	}

	if len(params.Messages) == 0 {
		return "", errors.New("no messages to send")
	}

	if opts.Stream {
		return c.completeStreaming(ctx, params, onToken)
	}
	return c.completeStandard(ctx, params)
}

func (c *Client) completeStandard(ctx context.Context, params openai.ChatCompletionNewParams) (string, error) {
	response, err := c.client.Chat.Completions.New(ctx, params)
	if err != nil {
		return "", err
	}

	if len(response.Choices) == 0 {
		return "", errors.New("received a response with no choices")
	}

	content := strings.TrimSpace(response.Choices[0].Message.Content)
	if content == "" {
		return "", errors.New("received a response with empty message content")
	}

	return content, nil
}

func (c *Client) completeStreaming(
	ctx context.Context,
	params openai.ChatCompletionNewParams,
	onToken func(string),
) (string, error) {
	stream := c.client.Chat.Completions.NewStreaming(ctx, params)

	var builder strings.Builder
	for stream.Next() {
		chunk := stream.Current()
		if len(chunk.Choices) == 0 {
			continue
		}

		token := chunk.Choices[0].Delta.Content
		if token == "" {
			continue
		}

		builder.WriteString(token)
		if onToken != nil {
			onToken(token)
		}
	}

	if err := stream.Err(); err != nil {
		return "", err
	}

	content := strings.TrimSpace(builder.String())
	if content == "" {
		return "", errors.New("received a streaming response with empty content")
	}

	return content, nil
}

func toOpenAIMessages(messages []Message) []openai.ChatCompletionMessageParamUnion {
	params := make([]openai.ChatCompletionMessageParamUnion, 0, len(messages))

	for _, message := range messages {
		content := strings.TrimSpace(message.Content)
		if content == "" {
			continue
		}

		switch strings.ToLower(strings.TrimSpace(message.Role)) {
		case roleSystem:
			params = append(params, openai.SystemMessage(content))
		case roleAssistant:
			params = append(params, openai.AssistantMessage(content))
		case roleUser:
			params = append(params, openai.UserMessage(content))
		default:
			params = append(params, openai.UserMessage(content))
		}
	}

	return params
}
