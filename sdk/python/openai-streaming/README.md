# GonkaGate Python OpenAI SDK Streaming Example

Minimal runnable Python example that streams `chat.completions` tokens from GonkaGate via the official OpenAI Python SDK.

## Why this example

- Validate real-time token streaming in Python.
- Confirm OpenAI-compatible model and endpoint setup.
- Reuse this script in CLI and backend streaming tools.

## Prerequisites

- Python 3.10+
- pip
- GonkaGate API key

## Quick Start

```bash
cd sdk/python/openai-streaming
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Smoke check (no network request):

```bash
python3 main.py --smoke
```

Streaming request:

```bash
python3 main.py
```

Custom prompt:

```bash
python3 main.py "Explain GonkaGate in one short sentence."
```

Optional syntax check:

```bash
python3 -m compileall .
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
- [Python SDK Documentation](https://gonkagate.com/en/docs/sdk/python)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Python chat: [sdk/python/openai-chat/README.md](../openai-chat/README.md)
- SDK index: [sdk/README.md](../../README.md)
- LangChain streaming: [examples/agents/langchain-streaming-agent/README.md](../../../examples/agents/langchain-streaming-agent/README.md)
