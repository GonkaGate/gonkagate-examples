# GonkaGate cURL Chat Completions Example

Minimal OpenAI-compatible `chat/completions` example for GonkaGate with regular and streaming requests.

## Why this example

- Fastest way to validate your API key and model.
- No SDK dependency, only HTTP with `curl`.
- Useful baseline for debugging request and response payloads.

## Prerequisites

- `bash`
- `curl`
- optional: `jq` for pretty JSON and stream parsing

## Quick Start

```bash
cd sdk/curl/chat-completions
cp .env.example .env
```

Edit `.env` and set at least:

```bash
GONKAGATE_API_KEY=YOUR_KEY
GONKAGATE_MODEL=YOUR_MODEL
```

Load variables:

```bash
set -a
source .env
set +a
```

## Environment Variables

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Regular completion:

```bash
chmod +x run.sh stream.sh
./run.sh
```

Streaming completion:

```bash
./stream.sh
```

## Expected Output

`./run.sh`:

- prints JSON response
- includes assistant text in `choices[0].message.content`

`./stream.sh`:

- streams assistant text token by token
- exits cleanly after `[DONE]`

## Troubleshooting

- `401 Unauthorized`: invalid or missing API key
- `402 Payment Required`: insufficient balance or billing issue
- `429 Too Many Requests`: retry with backoff
- `503 Service Unavailable`: temporary upstream issue, retry later

Both scripts print response body on non-2xx statuses.

## Learn More

- [Gonka API](https://gonkagate.com/en/gonka-api)
- [GonkaGate SDK Overview](https://gonkagate.com/en/docs/sdk)
- [cURL Integration Path (Quickstart)](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- SDK index: [sdk/README.md](../../README.md)
- Node.js chat example: [sdk/node/openai-chat/README.md](../../node/openai-chat/README.md)
- Node.js streaming example: [sdk/node/openai-streaming/README.md](../../node/openai-streaming/README.md)
