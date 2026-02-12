package errors

import (
	stdErrors "errors"
	"strings"

	openai "github.com/openai/openai-go/v3"
)

func FriendlyError(err error) string {
	if err == nil {
		return ""
	}

	var apiErr *openai.Error
	if stdErrors.As(err, &apiErr) {
		switch apiErr.StatusCode {
		case 401:
			return "401 Unauthorized. Check your API key."
		case 402:
			return "402 Payment Required. Check your GonkaGate balance or billing status."
		case 429:
			return "429 Too Many Requests. Slow down request rate and retry."
		case 503:
			return "503 Service Unavailable. Retry in a few seconds."
		}
	}

	message := strings.TrimSpace(err.Error())
	if message == "" {
		return "Unknown request error."
	}

	return message
}
