# GonkaGate LangChain Streaming Agent Example (Node.js)

Minimal runnable LangChain streaming agent example using GonkaGate OpenAI-compatible API.

## Why this example

- Validate streaming model tokens and tool-loop updates.
- Test real-time agent output behavior end to end.
- Reuse streaming patterns in production apps.

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key
- Model with tool-calling support

## Quick Start

```bash
cd examples/agents/langchain-streaming-agent
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
npm run start -- "What is 7 * 8? Use tool output and explain in one short sentence."
```

## Expected Output

Smoke:

```text
Smoke check passed: script can start and parse configuration.
```

Run:

```text
[update] model
[update] tools
[update] model
Agent stream: 7 * 8 = 56.
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
- [TypeScript SDK Documentation](https://gonkagate.com/en/docs/sdk/typescript)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- LangChain basic: [examples/agents/langchain-basic-agent/README.md](../langchain-basic-agent/README.md)
- LangChain structured output: [examples/agents/langchain-structured-output-agent/README.md](../langchain-structured-output-agent/README.md)
- Node SDK streaming: [sdk/node/openai-streaming/README.md](../../../sdk/node/openai-streaming/README.md)
