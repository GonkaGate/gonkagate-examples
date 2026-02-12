# GonkaGate Java OpenAI SDK Streaming Example

Minimal runnable Java example that streams `chat.completions` tokens from GonkaGate using the official OpenAI Java SDK.

## Why this example

- Validate token streaming in Java projects.
- Confirm OpenAI-compatible configuration for real-time outputs.
- Reuse this flow for server-side streaming endpoints.

## Prerequisites

- Java 21+
- Maven 3.9+
- GonkaGate API key

## Quick Start

```bash
cd sdk/java/openai-streaming
mvn -q -DskipTests compile
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

Streaming request:

```bash
mvn -q exec:java
```

Custom prompt:

```bash
mvn -q exec:java -Dexec.args="Explain GonkaGate in one short sentence."
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
- [Java SDK Documentation](https://gonkagate.com/en/docs/sdk/java)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Java chat: [sdk/java/openai-chat/README.md](../openai-chat/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Go streaming: [sdk/go/openai-streaming/README.md](../../go/openai-streaming/README.md)
