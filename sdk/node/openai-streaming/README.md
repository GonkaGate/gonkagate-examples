# GonkaGate Node.js OpenAI SDK Streaming Example

Minimal runnable Node.js example that streams `chat.completions` tokens from GonkaGate using the official OpenAI Node SDK.

## Why this example

- Validate streaming responses in Node.js.
- Confirm OpenAI-compatible endpoint and model setup.
- Reuse this flow in chat backends and event streams.

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key

## Quick Start

```bash
cd sdk/node/openai-streaming
npm install
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

Streaming request:

```bash
npm run start
```

Custom prompt:

```bash
npm run start -- "Explain GonkaGate in one short sentence."
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
- [TypeScript SDK Documentation](https://gonkagate.com/en/docs/sdk/typescript)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Node.js chat: [sdk/node/openai-chat/README.md](../openai-chat/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Next.js chat app: [examples/nextjs-vercel-ai-sdk-chat/README.md](../../../examples/nextjs-vercel-ai-sdk-chat/README.md)
