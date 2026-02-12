# GonkaGate LangChain Structured Output Agent Example (Node.js)

Minimal runnable LangChain agent example using GonkaGate OpenAI-compatible API to return validated JSON with `responseFormat`.

## Why this example

- Validate structured output extraction from model responses.
- Test schema-constrained payloads for automation workflows.
- Reuse this pattern for lead parsing and form processing.

## Prerequisites

- Node.js 18+
- npm
- GonkaGate API key
- Model with structured output or tool-calling support

## Quick Start

```bash
cd examples/agents/langchain-structured-output-agent
npm install
cp .env.example .env
npm run smoke
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Default extraction prompt:

```bash
npm run start
```

Custom extraction prompt:

```bash
npm run start -- "Extract lead data from: My name is Bob, email bob@example.com, company Globex, urgent production issue."
```

## Expected Output

Smoke:

```text
Smoke check passed: script can start and parse configuration.
```

Run:

```text
Structured response:
{
  "name": "Alice Johnson",
  "email": "alice@acme.dev",
  "company": "Acme Labs",
  "urgency": "high",
  "summary": "Alice Johnson from Acme Labs requests onboarding this week."
}
```

## Troubleshooting

- `401`: invalid key
- `402`: billing or balance issue
- `429`: rate limit
- `503`: temporary service issue
- structured output schema mismatch

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
- LangChain streaming: [examples/agents/langchain-streaming-agent/README.md](../langchain-streaming-agent/README.md)
- Node SDK chat: [sdk/node/openai-chat/README.md](../../../sdk/node/openai-chat/README.md)
