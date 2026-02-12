# GonkaGate Go Chat CLI Docker Image

Interactive **Go-based terminal chat CLI** image for the GonkaGate OpenAI-compatible API.

For authentication setup, model guidance, and API basics, see the **[Gonka API integration guide](https://gonkagate.com/en/gonka-api)**.

## Use Cases

- CLI-based chat testing without local Go install
- Quick OpenAI-compatible endpoint verification
- Interactive terminal demos with streaming output

## Quick Start

```bash
docker run -it --rm \
  -e GONKAGATE_API_KEY=your_api_key \
  -e GONKAGATE_MODEL=your_model \
  gonkagate/gonkagate-chat-cli:latest
```

Smoke check:

```bash
docker run --rm \
  -e GONKAGATE_API_KEY=placeholder \
  -e GONKAGATE_MODEL=placeholder \
  gonkagate/gonkagate-chat-cli:latest chat --smoke
```

## Tags

- `latest`
- `<semver>` (for example `0.1.0`)
- `<major>` (for example `0`, `1`, `2`)

## Key Environment Variables

- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_MODEL` (required)

## Source

- Repository: https://github.com/gonkagate/gonkagate-examples
- Example folder: `examples/gonkagate-chat-cli`
