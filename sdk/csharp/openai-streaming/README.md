# GonkaGate C# OpenAI SDK Streaming Example

Minimal runnable C# example that streams `chat.completions` tokens from GonkaGate via the official OpenAI .NET SDK.

## Why this example

- Validate token streaming behavior in .NET.
- Confirm OpenAI-compatible endpoint setup with minimal code.
- Reuse streaming flow for chat APIs and assistants.
- Keep production-friendly structure with small focused classes (config, CLI args, streaming service, error handler).

## Prerequisites

- .NET 8 SDK+
- GonkaGate API key

## Quick Start

```bash
cd sdk/csharp/openai-streaming
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

Streaming request:

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
- [.NET SDK Documentation](https://gonkagate.com/en/docs/sdk/dotnet)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- C# chat: [sdk/csharp/openai-chat/README.md](../openai-chat/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Node streaming: [sdk/node/openai-streaming/README.md](../../node/openai-streaming/README.md)
