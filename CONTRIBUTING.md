# Contributing

Thanks for helping improve `gonkagate-examples`.

This repository is intentionally example-first: small, runnable, and copy-paste friendly.

## Before You Start

- Read `docs/repo-structure.md` for taxonomy and naming rules.
- Pick one focused change per PR (one example or one clear docs improvement).
- Do not commit secrets. Use placeholders in `.env.example` only.

## Add or Update an Example

Every example directory must include:

- `README.md`
- `.env.example`
- Source files required to run independently

Path conventions:

- SDK quickstarts: `sdk/<language>/<example-name>/`
- Production-style examples: `examples/<example-name>/`
- Agent examples (exception): `examples/agents/<example-name>/`

Naming:

- `sdk` language folder must be one of: `csharp`, `curl`, `go`, `java`, `node`, `python`
- Example folders must be `kebab-case`

## Local Validation

Run checks relevant to the example you touched. There is no global build/test pipeline.

Examples:

- Python smoke: `python -m compileall <path>`
- Node example: `npm run build` and/or `npm test` (if provided)
- Any example: run its exact setup and run commands from that example `README.md`

Recommended structure check before opening PR:

```bash
bash -lc '
set -euo pipefail
for d in sdk/*/*; do
  [ -d "$d" ] || continue
  [ -f "$d/README.md" ] && [ -f "$d/.env.example" ]
done
for d in examples/*; do
  [ -d "$d" ] || continue
  if [ "$(basename "$d")" = "agents" ]; then
    for a in "$d"/*; do [ -d "$a" ] || continue; [ -f "$a/README.md" ] && [ -f "$a/.env.example" ]; done
  else
    [ -f "$d/README.md" ] && [ -f "$d/.env.example" ]
  fi
done
'
```

## Pull Request Checklist

- Update root `README.md` index if you added/moved examples.
- Keep run instructions and expected output current in example `README.md`.
- Keep `.env.example` minimal and secret-free.
- Include purpose, run steps, and expected result in the PR description.

