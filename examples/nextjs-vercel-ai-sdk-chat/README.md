# GonkaGate Next.js Vercel AI SDK Chat Example (Streaming)

Minimal runnable Next.js 16 chat UI example using Vercel AI SDK with GonkaGate OpenAI-compatible API.

## Why this example

- Validate real-time token streaming in a web UI.
- Demonstrate server-side API key handling in Next.js.
- Provide a production-style chat baseline for web products.

## Prerequisites

- Node.js 20.9+
- npm
- GonkaGate API key

## Quick Start

```bash
cd examples/nextjs-vercel-ai-sdk-chat
npm install
cp .env.example .env
npm run smoke
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Start local app:

```bash
npm run dev
```

Open `http://localhost:3000` and send a message.

Production build check:

```bash
npm run build
```

## Expected Output

Smoke:

```text
> tsc --noEmit
```

UI behavior:

- chat input and send button are visible
- user message appears immediately
- assistant response streams token by token

## Troubleshooting

The server route (`app/api/chat/route.ts`) returns clear messages for:

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

- Examples index: [examples/README.md](../README.md)
- Node SDK streaming: [sdk/node/openai-streaming/README.md](../../sdk/node/openai-streaming/README.md)
- LangChain streaming agent: [examples/agents/langchain-streaming-agent/README.md](../agents/langchain-streaming-agent/README.md)
