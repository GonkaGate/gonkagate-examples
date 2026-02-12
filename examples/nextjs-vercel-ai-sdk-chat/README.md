# GonkaGate Web Chat (Next.js + Vercel AI SDK)

Beginner-friendly web chat example with streaming responses from GonkaGate using a Next.js app and the Vercel AI SDK.

## Why this example

- Lets you run a real chat UI locally in a few minutes.
- Shows how to keep your API key on the server side (`app/api/chat/route.ts`).
- Demonstrates token-by-token streaming in the browser.

## Features

- Minimal chat UI (input, send button, message list)
- Streaming assistant responses
- Simple smoke check (`npm run smoke`)
- Explicit API error handling for `401`, `402`, `429`, `503`

## Prerequisites

- Node.js `20.9+`
- npm (comes with Node.js installer)
- GonkaGate API key
- Terminal (PowerShell on Windows, Terminal on macOS, shell on Linux)

Install Node.js and npm:

- Node.js download page: https://nodejs.org/en/download
- npm installation guide: https://docs.npmjs.com/downloading-and-installing-node-js-and-npm

After installation, verify:

```bash
node -v
npm -v
```

You should see Node version `v20.9.0` or newer.

## Quick Start (For Beginners)

### Step 1. Open terminal and go to this example folder

If you already cloned the repo:

```bash
cd gonkagate-examples/examples/nextjs-vercel-ai-sdk-chat
```

### Step 2. Install dependencies

```bash
npm install
```

### Step 3. Create `.env` from template

macOS/Linux:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### Step 4. Put your key and model into `.env`

Open `.env` in any text editor and set:

- `GONKAGATE_API_KEY=...`
- `GONKAGATE_MODEL=...`

Optional fallbacks (OpenAI-compatible naming) are also supported:

- `OPENAI_API_KEY`
- `OPENAI_MODEL`

Base URL is fixed to `https://api.gonkagate.com/v1`.

### Step 5. Run smoke check (no chat request yet)

```bash
npm run smoke
```

Expected result includes:

```text
tsc --noEmit
```

### Step 6. Start local app

```bash
npm run dev
```

Open `http://localhost:3000`.

### Step 7. Send your first message

Type a prompt in the chat input and click **Send**.
You should see the assistant response streaming gradually.

## Environment Variables

In `.env`:

- `GONKAGATE_API_KEY` (required, unless `OPENAI_API_KEY` is set)
- `GONKAGATE_MODEL` (required, unless `OPENAI_MODEL` is set)

Notes:

- `GONKAGATE_BASE_URL` and `OPENAI_BASE_URL` are ignored in this example.
- The API base URL is always `https://api.gonkagate.com/v1`.

## Run

Development server:

```bash
npm run dev
```

Type check:

```bash
npm run smoke
```

Production build check:

```bash
npm run build
```

Run production server (after build):

```bash
npm run start
```

## Expected Output

Smoke check:

```text
> vercel-ai-sdk-chat@0.1.0 smoke
> npm run typecheck
...
> tsc --noEmit
```

UI behavior:

- Chat input and send button are visible
- Your message appears immediately
- Assistant response streams token by token

## Troubleshooting

- `node: command not found` or `npm: command not found`
  - Node.js/npm are not installed or not in PATH. Install from https://nodejs.org/en/download and reopen terminal.
- `Missing API key: set GONKAGATE_API_KEY...`
  - Add `GONKAGATE_API_KEY` (or `OPENAI_API_KEY`) to `.env`.
- `Missing model: set GONKAGATE_MODEL...`
  - Add `GONKAGATE_MODEL` (or `OPENAI_MODEL`) to `.env`.
- `401`
  - Invalid API key.
- `402`
  - Billing or balance issue.
- `429`
  - Rate limit exceeded. Retry later.
- `503`
  - Temporary upstream issue. Retry later.

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
