# Security Policy

## Reporting a Vulnerability

Please do not open public GitHub issues for security vulnerabilities.

Report security issues to:

- `support@gonkagate.com`

Include:

- affected paths/examples
- reproduction steps
- impact summary
- suggested fix (optional)

## Supported Versions

Security fixes are provided for the current state of the `main` branch.

## Secrets Handling

- Never commit real API keys, tokens, or credentials.
- Only placeholders belong in `.env.example`.
- If a secret is exposed, rotate it immediately.

