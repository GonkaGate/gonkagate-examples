# GonkaGate LangChain Basic Agent Example (Node.js)

Minimal runnable LangChain agent example using GonkaGate as an OpenAI-compatible API provider.

## Why this example

- Validate tool-calling agent flow with minimal setup.
- Confirm model and API key configuration.
- Use as a baseline for production agent backends.

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key
- Model with tool-calling support

## Quick Start

```bash
cd examples/agents/langchain-basic-agent
npm install
cp .env.example .env
npm run smoke
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required, supports tool calling)

## Run

Default prompt:

```bash
npm run start
```

Custom prompt:

```bash
npm run start -- "What time is it in UTC? Use tool output in your final answer."
```

## Expected Output

Smoke:

```text
Smoke check passed: script can start and parse configuration.
```

Run:

```text
Agent response:
21 + 21 = 42.
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

- LangChain streaming: [examples/agents/langchain-streaming-agent/README.md](../langchain-streaming-agent/README.md)
- LangChain structured output: [examples/agents/langchain-structured-output-agent/README.md](../langchain-structured-output-agent/README.md)
- Node SDK chat: [sdk/node/openai-chat/README.md](../../../sdk/node/openai-chat/README.md)
