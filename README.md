# GonkaGate Examples: OpenAI-Compatible SDK and Production Integration Examples

This repository provides runnable GonkaGate examples for developers who need a fast path from API key to production-ready integration.

GonkaGate is an OpenAI-compatible API gateway to Gonka Network with USD billing. Most integrations work by changing `api_key` and `base_url/baseURL`.

## Why this repository

- Reduce time to first successful request to 5-10 minutes.
- Validate chat and streaming flows with copy-paste commands.
- Reuse production-style examples for web apps and agents.
- Standardize handling for `401`, `402`, `429`, and `503` errors.

## Quick Start

1. Clone this repository.
2. Choose an example from `sdk/` or `examples/`.
3. Copy `.env.example` to `.env` in that folder.
4. Set `GONKAGATE_API_KEY` and `GONKAGATE_MODEL`.
5. Run the commands from that example README.

## Repository Structure

```text
/
  README.md
  AGENTS.md
  .github/
    workflows/ci.yml
  sdk/
    <language>/<example-name>/
  examples/
    <example-name>/
    agents/<example-name>/
```

## SDK Quickstarts

- [cURL Chat Completions](sdk/curl/chat-completions/README.md)
- [C# OpenAI Chat](sdk/csharp/openai-chat/README.md)
- [C# OpenAI Streaming](sdk/csharp/openai-streaming/README.md)
- [Go OpenAI Chat](sdk/go/openai-chat/README.md)
- [Go OpenAI Streaming](sdk/go/openai-streaming/README.md)
- [Java OpenAI Chat](sdk/java/openai-chat/README.md)
- [Java OpenAI Streaming](sdk/java/openai-streaming/README.md)
- [Node.js OpenAI Chat](sdk/node/openai-chat/README.md)
- [Node.js OpenAI Streaming](sdk/node/openai-streaming/README.md)
- [Python OpenAI Chat](sdk/python/openai-chat/README.md)
- [Python OpenAI Streaming](sdk/python/openai-streaming/README.md)

## Production-Style Examples

- [Next.js Vercel AI SDK Chat](examples/nextjs-vercel-ai-sdk-chat/README.md)
- [Cross-Platform CLI Chat (Go)](examples/gonkagate-chat-cli/README.md)
- [LangChain Basic Agent](examples/agents/langchain-basic-agent/README.md)
- [LangChain Streaming Agent](examples/agents/langchain-streaming-agent/README.md)
- [LangChain Structured Output Agent](examples/agents/langchain-structured-output-agent/README.md)

## SDK Docs by Language

- [Python SDK docs](https://gonkagate.com/en/docs/sdk/python)
- [TypeScript SDK docs (Node.js)](https://gonkagate.com/en/docs/sdk/typescript)
- [Go SDK docs](https://gonkagate.com/en/docs/sdk/go)
- [.NET SDK docs (C#)](https://gonkagate.com/en/docs/sdk/dotnet)
- [Java SDK docs](https://gonkagate.com/en/docs/sdk/java)
- [cURL setup path](https://gonkagate.com/en/docs/quickstart)

## Contribution Standard

Each example folder should include:

- `README.md` with setup, run commands, expected output, and troubleshooting.
- `.env.example` with placeholders only.
- Self-contained source files that run independently.

## Learn More

- [Gonka API](https://gonkagate.com/en/gonka-api)
- [GonkaGate SDK Overview](https://gonkagate.com/en/docs/sdk)
- [Quickstart Guide](https://gonkagate.com/en/docs/quickstart)
- [API Reference](https://gonkagate.com/en/docs/api)
- [Available Models](https://gonkagate.com/en/models)
- [Pricing](https://gonkagate.com/en/pricing)
- [Public Chat Playground](https://gonkagate.com/en/chat)

## Related Pages

- SDK index: [sdk/README.md](sdk/README.md)
- Examples index: [examples/README.md](examples/README.md)
- Contributing guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Support: [SUPPORT.md](SUPPORT.md)
- Security policy: [SECURITY.md](SECURITY.md)

## License

Apache-2.0. See `LICENSE`.
