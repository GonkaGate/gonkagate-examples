# GonkaGate Java OpenAI SDK Chat Completion Example

Minimal runnable Java example that sends one chat completion request to GonkaGate using the official OpenAI Java SDK.

## Why this example

- Validate OpenAI-compatible chat integration for Java services.
- Confirm environment and endpoint configuration in Maven projects.
- Provide a stable baseline for production adapters.

## Prerequisites

- Java 21+
- Maven 3.9+
- GonkaGate API key

## Quick Start

```bash
cd sdk/java/openai-chat
mvn -q -DskipTests compile
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
mvn -q exec:java -Dexec.args="--smoke"
```

Real API request:

```bash
mvn -q exec:java
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
- [Java SDK Documentation](https://gonkagate.com/en/docs/sdk/java)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Java streaming: [sdk/java/openai-streaming/README.md](../openai-streaming/README.md)
- SDK index: [sdk/README.md](../../README.md)
- C# chat: [sdk/csharp/openai-chat/README.md](../../csharp/openai-chat/README.md)
