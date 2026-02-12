package storage

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"time"
)

type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

type Metadata struct {
	Model        string  `json:"model"`
	BaseURL      string  `json:"base_url"`
	Streaming    bool    `json:"streaming"`
	Temperature  float64 `json:"temperature"`
	SystemPrompt string  `json:"system_prompt"`
}

type Transcript struct {
	SavedAt  string    `json:"saved_at"`
	Meta     Metadata  `json:"meta"`
	Messages []Message `json:"messages"`
}

func Save(path string, transcript Transcript) error {
	cleanPath := filepath.Clean(path)
	directory := filepath.Dir(cleanPath)

	if directory != "." {
		if err := os.MkdirAll(directory, 0o755); err != nil {
			return fmt.Errorf("create save directory: %w", err)
		}
	}

	if transcript.SavedAt == "" {
		transcript.SavedAt = time.Now().UTC().Format(time.RFC3339)
	}

	data, err := json.MarshalIndent(transcript, "", "  ")
	if err != nil {
		return fmt.Errorf("marshal transcript: %w", err)
	}

	data = append(data, '\n')
	if err := os.WriteFile(cleanPath, data, 0o644); err != nil {
		return fmt.Errorf("write transcript: %w", err)
	}

	return nil
}
