# GonkaGate Agent Examples (LangChain)

Runnable LangChain agent examples using the GonkaGate OpenAI-compatible API.

## Why these agent examples

- Validate tool-calling integration quickly.
- Compare baseline, streaming, and structured output patterns.
- Reuse agent flows in production backends.

## Available Examples

- [LangChain Basic Agent](langchain-basic-agent/README.md)
- [LangChain Streaming Agent](langchain-streaming-agent/README.md)
- [LangChain Structured Output Agent](langchain-structured-output-agent/README.md)

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key
- A model with tool calling support

## Common Environment Variables

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Quick Start

```bash
cd examples/agents/<example-name>
npm install
cp .env.example .env
npm run smoke
npm run start
```

## Learn More

- [Gonka API](https://gonkagate.com/en/gonka-api)
- [GonkaGate SDK Overview](https://gonkagate.com/en/docs/sdk)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Agents index: [examples/agents/README.md](README.md)
- Next.js chat app: [examples/nextjs-vercel-ai-sdk-chat/README.md](../nextjs-vercel-ai-sdk-chat/README.md)
- SDK Node examples: [sdk/node/openai-chat/README.md](../../sdk/node/openai-chat/README.md)
