package chat

import "strings"

const (
	roleSystem    = "system"
	roleUser      = "user"
	roleAssistant = "assistant"
)

type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

type Session struct {
	systemPrompt string
	messages     []Message
}

func NewSession(systemPrompt string) *Session {
	prompt := strings.TrimSpace(systemPrompt)
	if prompt == "" {
		prompt = "You are a concise assistant."
	}

	s := &Session{systemPrompt: prompt}
	s.Reset()
	return s
}

func (s *Session) AddUser(content string) {
	trimmed := strings.TrimSpace(content)
	if trimmed == "" {
		return
	}

	s.messages = append(s.messages, Message{Role: roleUser, Content: trimmed})
}

func (s *Session) AddAssistant(content string) {
	trimmed := strings.TrimSpace(content)
	if trimmed == "" {
		return
	}

	s.messages = append(s.messages, Message{Role: roleAssistant, Content: trimmed})
}

func (s *Session) RemoveLastUserMessage() {
	if len(s.messages) == 0 {
		return
	}

	lastIndex := len(s.messages) - 1
	if s.messages[lastIndex].Role != roleUser {
		return
	}

	s.messages = s.messages[:lastIndex]
}

func (s *Session) Reset() {
	s.messages = []Message{{Role: roleSystem, Content: s.systemPrompt}}
}

func (s *Session) Messages() []Message {
	copied := make([]Message, len(s.messages))
	copy(copied, s.messages)
	return copied
}

func (s *Session) SystemPrompt() string {
	return s.systemPrompt
}
