# GonkaGate Go OpenAI SDK Chat Completion Example

Minimal runnable Go example that sends one chat completion request to GonkaGate via the official OpenAI Go SDK.

## Why this example

- Validate OpenAI-compatible chat calls in Go services.
- Test environment loading and model configuration.
- Keep a clean baseline for API integration.

## Prerequisites

- Go 1.22+
- GonkaGate API key

## Quick Start

```bash
cd sdk/go/openai-chat
go mod tidy
```

For real requests, create `.env`:

```bash
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

Real API request:

```bash
go run .
```

## Expected Output

Smoke:

```text
Smoke check passed: script can start and parse configuration.
```

Run:

```text
Model response:
Hi from GonkaGate! Nice to meet you.
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

- Go streaming: [sdk/go/openai-streaming/README.md](../openai-streaming/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Python chat: [sdk/python/openai-chat/README.md](../../python/openai-chat/README.md)
