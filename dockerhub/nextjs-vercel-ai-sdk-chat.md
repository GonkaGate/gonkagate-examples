# GonkaGate Next.js Chat Docker Image (Vercel AI SDK)

Production-ready example image for a **Next.js chat UI with streaming responses** using the GonkaGate OpenAI-compatible API.

If you need API setup, model selection, and integration details, start with the **[Gonka API developer docs](https://gonkagate.com/en/gonka-api)**.

## Use Cases

- Next.js + Vercel AI SDK chat demo in Docker
- OpenAI-compatible migration testing with `GONKAGATE_API_KEY`
- Fast local proof-of-concept for streaming chat UX

## Quick Start

```bash
docker run --rm -p 3000:3000 \
  -e GONKAGATE_API_KEY=your_api_key \
  -e GONKAGATE_MODEL=your_model \
  gonkagate/nextjs-vercel-ai-sdk-chat:latest
```

Open `http://localhost:3000` and send a message.

## Tags

- `latest`
- `<semver>` (for example `0.1.0`)
- `<major>` (for example `1`, `2`; published for major versions `>= 1`)

## Key Environment Variables

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Source

- Repository: https://github.com/gonkagate/gonkagate-examples
- Example folder: `examples/nextjs-vercel-ai-sdk-chat`
