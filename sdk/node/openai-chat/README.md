# GonkaGate Node.js OpenAI SDK Chat Completion Example

Minimal runnable Node.js example that sends one chat completion request to GonkaGate using the official OpenAI Node SDK.

## Why this example

- Validate OpenAI-compatible chat setup in Node.js quickly.
- Confirm API key configuration.
- Use this baseline for backend services and scripts.

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key

## Quick Start

```bash
cd sdk/node/openai-chat
npm install
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
npm run smoke
```

Real API request:

```bash
npm run start
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
- [TypeScript SDK Documentation](https://gonkagate.com/en/docs/sdk/typescript)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Node.js streaming: [sdk/node/openai-streaming/README.md](../openai-streaming/README.md)
- SDK index: [sdk/README.md](../../README.md)
- LangChain basic agent: [examples/agents/langchain-basic-agent/README.md](../../../examples/agents/langchain-basic-agent/README.md)
