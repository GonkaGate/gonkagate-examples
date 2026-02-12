# GonkaGate C# OpenAI SDK Chat Completion Example

Minimal runnable C# example that sends one `chat.completions` request to GonkaGate through the official OpenAI .NET SDK.

## Why this example

- Validate OpenAI-compatible chat integration in .NET quickly.
- Confirm API key wiring for C# backends.
- Start from a small script and extend for production flows.

## Prerequisites

- .NET 8 SDK+
- GonkaGate API key

## Quick Start

```bash
cd sdk/csharp/openai-chat
cp .env.example .env
dotnet restore
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Smoke check (no network request):

```bash
dotnet run -- --smoke
```

Real API request:

```bash
dotnet run --
```

Custom prompt:

```bash
dotnet run -- "Explain GonkaGate in one short sentence."
```

Optional build check:

```bash
dotnet build
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
- [.NET SDK Documentation](https://gonkagate.com/en/docs/sdk/dotnet)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- C# streaming: [sdk/csharp/openai-streaming/README.md](../openai-streaming/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Go chat: [sdk/go/openai-chat/README.md](../../go/openai-chat/README.md)
