# Docker Release Setup

This repository supports automatic Docker image tagging and publishing for:

- `gonkagate/nextjs-vercel-ai-sdk-chat`
- `gonkagate/gonkagate-chat-cli`

## 1. Configure Docker Hub Token

1. Open Docker Hub -> Account Settings -> Personal access tokens.
2. Create a token with read/write access to your repositories.
3. In GitHub repository settings, add Actions secrets:
   - `DOCKERHUB_USERNAME`
   - `DOCKERHUB_TOKEN`

Never commit the token into the repository.

## 2. Create Docker Hub Repositories

Create these public repositories in Docker Hub namespace `gonkagate`:

- `nextjs-vercel-ai-sdk-chat`
- `gonkagate-chat-cli`

## 3. Auto-Tag Rules

Workflow: `.github/workflows/docker-auto-tag.yml`

On every push to `main`, the workflow inspects changes and creates tags:

- `nextjs-vX.Y.Z` for Next.js example changes
- `cli-vX.Y.Z` for CLI example changes

Version bump logic (Conventional Commits):

- `major`: commit subject contains `!` (for example `feat!: ...`) or body contains `BREAKING CHANGE`
- `minor`: commit subject starts with `feat:`
- `patch`: any other commit type affecting target files

Initial tag baseline (when no tags exist): `0.1.0`

## 4. Docker Publish Tags

Workflow: `.github/workflows/docker-publish.yml`

When `nextjs-vX.Y.Z` or `cli-vX.Y.Z` is pushed, Docker images are published with:

- exact `X.Y.Z`
- floating major `X`
- `latest` (only for stable `X.Y.Z` versions)

## 5. Manual Dry Run

You can run auto-tag manually from GitHub Actions:

- Workflow: `Docker Auto Tag`
- `dry_run: true` to preview tags without creating them
