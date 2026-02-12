# GonkaGate Go OpenAI SDK Streaming Example

Minimal runnable Go example that streams `chat.completions` tokens from GonkaGate via the official OpenAI Go SDK.

## Why this example

- Validate streaming token handling in Go.
- Confirm OpenAI-compatible setup for backend streaming flows.
- Reuse this baseline for SSE and real-time chat services.

## Prerequisites

- Go 1.22+
- GonkaGate API key

## Quick Start

```bash
cd sdk/go/openai-streaming
cp .env.example .env
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Smoke check (no network request):

```bash
go run . --smoke
```

Streaming request:

```bash
go run .
```

Custom prompt:

```bash
go run . "Explain GonkaGate in one short sentence."
```

Optional build check:

```bash
go build ./...
```

## Expected Output

Smoke:

```text
Smoke check passed: config is valid and script can start.
```

Run:

```text
Hi from GonkaGate...
[stream complete]
```

## Troubleshooting

- `401`: invalid key
- `402`: billing or balance issue
- `429`: rate limit
- `503`: temporary service issue

## Learn More

- [Gonka API](https://gonkagate.com/en/gonka-api)
- [GonkaGate SDK Overview](https://gonkagate.com/en/docs/sdk)
- [Go SDK Documentation](https://gonkagate.com/en/docs/sdk/go)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Go chat: [sdk/go/openai-chat/README.md](../openai-chat/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Java streaming: [sdk/java/openai-streaming/README.md](../../java/openai-streaming/README.md)
