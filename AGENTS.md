# Repository Guidelines

## Project Structure & Module Organization
This repository is intentionally small and example-focused. Keep each example isolated and runnable.

Current top-level directories:
- `.github/workflows/`: CI workflows (currently `ci.yml`).
- `sdk/`: minimal language quickstarts in `sdk/<language>/<example-name>/`.
- `examples/`: production-style runnable examples.

Current SDK quickstart directories:
- `sdk/curl/chat-completions`
- `sdk/csharp/openai-chat`
- `sdk/csharp/openai-streaming`
- `sdk/go/openai-chat`
- `sdk/go/openai-streaming`
- `sdk/java/openai-chat`
- `sdk/java/openai-streaming`
- `sdk/node/openai-chat`
- `sdk/node/openai-streaming`
- `sdk/python/openai-chat`
- `sdk/python/openai-streaming`

Current production-style example directories:
- `examples/gonkagate-chat-cli`
- `examples/nextjs-vercel-ai-sdk-chat`
- `examples/agents/langchain-basic-agent`
- `examples/agents/langchain-streaming-agent`
- `examples/agents/langchain-structured-output-agent`

Root documentation and policy files:
- `README.md`
- `AGENTS.md`
- `CONTRIBUTING.md`
- `SECURITY.md`
- `SUPPORT.md`
- `LICENSE`

Treat `node_modules`, `.next`, and `__pycache__` as local build artifacts, not canonical repo structure.

Each example directory must include at minimum:
- `README.md` (what it does, setup, run, expected output)
- `.env.example` (required variables, no secrets)
- Source files needed to run independently

## Build, Test, and Development Commands
There is no single root build/test pipeline yet. Work from the specific example directory and follow its README.
- `cd sdk/<language>/<example-name>`: enter one SDK quickstart.
- `cd examples/<example-name>`: enter one production-style app example.
- `cd examples/agents/<example-name>`: enter one agent example.
- `rg --files sdk examples .github`: list tracked files quickly.
- `python -m compileall sdk/python`: lightweight Python smoke check for Python SDK examples.
- `npm run smoke` (when provided): quick Node/Next/LangChain smoke check.
- `npm run build` or `npm test` (when provided by an example): validate Node-based examples.
- `go build ./...` (from `examples/gonkagate-chat-cli`): lightweight Go build validation.

If you add a new example, document exact install/run commands in that example's `README.md`.

## Coding Style & Naming Conventions
- Use clear, minimal, copy-paste-friendly code.
- Use normalized language folder names in `sdk/` (`csharp`, `node`, `python`, `go`, `java`, `curl`).
- Prefer `kebab-case` for example folder names (for example, `openai-streaming`, `nextjs-vercel-ai-sdk-chat`).
- Keep environment variables uppercase snake case (for example, `GONKAGATE_API_KEY`).
- Preserve OpenAI-compatible naming (`base_url`/`baseURL`, `api_key`) where relevant.
- Do not commit real credentials; only placeholders in `.env.example`.

## Testing Guidelines
Testing is example-level, not monorepo-level.
- Verify the `clone -> env -> run` path works in 5-10 minutes.
- Include at least one smoke validation path per example (build, compile, or run command).
- Document expected output so contributors can confirm success quickly.

## Commit & Pull Request Guidelines
Current history is minimal (`Initial commit`), so use concise, imperative commit subjects.
- Good pattern: `docs: add node streaming example guide`
- Keep PRs focused on one example or one clear improvement.
- PRs should include: purpose, run steps, expected result, and linked issue (if available).
- Add screenshots/log snippets for UI or streaming behavior when useful.

## Security & Configuration Tips
Standardize on:
- `GONKAGATE_API_KEY` (required)
- `GONKAGATE_BASE_URL` (optional)
- `GONKAGATE_MODEL` (optional)
