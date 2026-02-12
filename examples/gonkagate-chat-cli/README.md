# GonkaGate CLI Chat (Go, Self-Build)

Beginner-friendly interactive CLI chat example for GonkaGate using the OpenAI-compatible Chat Completions API.

Binary name: `gonkagate-chat`

## Why this example

- Lets users build locally from source (no prebuilt binaries required).
- Supports interactive chat with runtime streaming toggle (`true`/`false`).
- Works on Windows, macOS, and Linux.

## Features

- Interactive chat loop in terminal
- `--stream true|false`
- Slash commands: `/help`, `/model`, `/stream`, `/reset`, `/save`, `/exit`
- Optional transcript save to JSON
- Uses `GONKAGATE_API_KEY` for authentication.
- Explicit handling for `401`, `402`, `429`, `503`
- Docker support (`Dockerfile` + `docker-compose.yml`)

## Prerequisites

- Go 1.22 or newer
- GonkaGate API key
- A terminal (PowerShell on Windows, Terminal on macOS, shell on Linux)
- Docker (optional, for containerized run)

Install Go:

- Official download page: https://go.dev/dl/
- After install, verify:

```bash
go version
```

You should see something like `go version go1.22.x ...` or newer.

## Quick Start (For Beginners)

### Step 1. Open terminal and go to this example folder

If you already cloned the repo:

```bash
cd gonkagate-examples/examples/gonkagate-chat-cli
```

### Step 2. Create `.env` from template

macOS/Linux:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### Step 3. Put your key/model into `.env`

Open `.env` in any text editor and set:

- `GONKAGATE_API_KEY=...`
- `GONKAGATE_MODEL=...`

Base URL is fixed to `https://api.gonkagate.com/v1`.

### Step 4. Build locally

macOS/Linux:

```bash
go mod tidy
go build -o gonkagate-chat .
```

Windows PowerShell:

```powershell
go mod tidy
go build -o gonkagate-chat.exe .
```

### Step 5. Smoke check (no network call)

macOS/Linux:

```bash
./gonkagate-chat chat --smoke
```

Windows PowerShell:

```powershell
.\gonkagate-chat.exe chat --smoke
```

Expected result:

```text
Smoke check passed: config is valid and command can start.
```

### Step 6. Start chat

macOS/Linux:

```bash
./gonkagate-chat chat --stream=true
```

Windows PowerShell:

```powershell
.\gonkagate-chat.exe chat --stream=true
```

Type your message after `you> ` and press Enter.

## Environment Variables

In `.env`:

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

Base URL is fixed to `https://api.gonkagate.com/v1`.

## Run

Run with streaming on:

```bash
./gonkagate-chat chat --stream=true
```

Run with streaming off:

```bash
./gonkagate-chat chat --stream=false
```

Windows equivalents:

```powershell
.\gonkagate-chat.exe chat --stream=true
.\gonkagate-chat.exe chat --stream=false
```

Set model explicitly:

```bash
./gonkagate-chat chat --model your_model
```

Auto-save history on exit:

```bash
./gonkagate-chat chat --save ./transcripts/session.json
```

## Docker

Build image locally:

```bash
docker build -t gonkagate/gonkagate-chat-cli:local .
```

Smoke check in container:

```bash
docker run --rm --env-file .env gonkagate/gonkagate-chat-cli:local chat --smoke
```

Interactive chat in container:

```bash
docker run -it --rm --env-file .env gonkagate/gonkagate-chat-cli:local
```

Run with Docker Compose:

```bash
docker compose run --rm cli chat --smoke
docker compose run --rm cli
```

Pull published image from Docker Hub:

```bash
docker pull gonkagate/gonkagate-chat-cli:latest
docker run -it --rm --env-file .env gonkagate/gonkagate-chat-cli:latest
```

Release tags are published as semantic versions (for example `0.1.0`), floating major tags (for example `0`, `1`, `2`), and `latest`.
CI publish trigger tag format: `cli-vX.Y.Z`.

## Slash Commands

- `/help` show available commands
- `/model <model-id>` change model for next requests
- `/stream on|off` toggle streaming mode
- `/reset` clear conversation history and keep system prompt
- `/save [path]` write transcript to JSON (default `gonkagate-chat-history.json`)
- `/exit` exit chat

## Expected Output

Startup:

```text
[info] Interactive chat started. Type /help for commands.
you>
```

Smoke:

```text
Smoke check passed: config is valid and command can start.
```

Sample chat:

```text
you> Explain GonkaGate in one short sentence.
assistant> GonkaGate is an OpenAI-compatible API gateway for Gonka Network.
```

## Troubleshooting

- `go: command not found`
  - Go is not installed or not in PATH. Reinstall Go from https://go.dev/dl/ and reopen terminal.
- `missing API key`
  - Fill `GONKAGATE_API_KEY` in `.env`.
- `missing model`
  - Fill `GONKAGATE_MODEL` in `.env`.
- `401`
  - Invalid key.
- `402`
  - Billing/balance issue.
- `429`
  - Rate limit; retry later.
- `503`
  - Temporary service issue; retry later.

## Learn More

- [Gonka API](https://gonkagate.com/en/gonka-api)
- [GonkaGate SDK Overview](https://gonkagate.com/en/docs/sdk)
- [Go SDK Documentation](https://gonkagate.com/en/docs/sdk/go)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Examples

- Go chat quickstart: [sdk/go/openai-chat/README.md](../../sdk/go/openai-chat/README.md)
- Go streaming quickstart: [sdk/go/openai-streaming/README.md](../../sdk/go/openai-streaming/README.md)
- Root examples index: [examples/README.md](../README.md)
