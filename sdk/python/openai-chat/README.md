# GonkaGate Python OpenAI SDK Chat Completion Example

Minimal runnable Python example that sends one chat completion request to GonkaGate via the official OpenAI Python SDK.

## Why this example

- Validate OpenAI-compatible setup in Python quickly.
- Confirm environment loading and API connectivity.
- Use this script as a baseline for backend services.

## Prerequisites

- Python 3.10+
- pip
- GonkaGate API key

## Quick Start

```bash
cd sdk/python/openai-chat
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

On Windows PowerShell:

```powershell
.venv\Scripts\Activate.ps1
```

For real requests, create `.env`:

```bash
cp .env.example .env
```

## Environment Variables

Set in `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Run

Smoke check (no network request):

```bash
python main.py --smoke
```

Real API request:

```bash
python main.py
```

## Expected Output

Smoke:

```text
Smoke check passed: script can start and parse configuration.
```

Run:

```text
Model response:
Hi from GonkaGate! Nice to meet you.
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

- Python streaming: [sdk/python/openai-streaming/README.md](../openai-streaming/README.md)
- SDK index: [sdk/README.md](../../README.md)
- Go chat: [sdk/go/openai-chat/README.md](../../go/openai-chat/README.md)
