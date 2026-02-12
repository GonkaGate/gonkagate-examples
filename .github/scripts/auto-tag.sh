#!/usr/bin/env bash
set -euo pipefail

BASELINE_VERSION="${BASELINE_VERSION:-0.1.0}"
DRY_RUN="${DRY_RUN:-false}"
PUSH_TAGS="${PUSH_TAGS:-true}"

is_true() {
  case "${1,,}" in
    1|true|yes|on) return 0 ;;
    *) return 1 ;;
  esac
}

require_semver() {
  local version="$1"
  if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Invalid semver: $version" >&2
    exit 1
  fi
}

bump_version() {
  local current="$1"
  local bump="$2"
  local major minor patch

  IFS='.' read -r major minor patch <<<"$current"

  case "$bump" in
    major)
      major=$((major + 1))
      minor=0
      patch=0
      ;;
    minor)
      minor=$((minor + 1))
      patch=0
      ;;
    patch)
      patch=$((patch + 1))
      ;;
    *)
      echo "Unsupported bump type: $bump" >&2
      exit 1
      ;;
  esac

  echo "${major}.${minor}.${patch}"
}

target_prefix() {
  case "$1" in
    nextjs) echo "nextjs-v" ;;
    cli) echo "cli-v" ;;
    *)
      echo "Unknown target: $1" >&2
      exit 1
      ;;
  esac
}

target_paths() {
  case "$1" in
    nextjs)
      cat <<'EOF'
examples/nextjs-vercel-ai-sdk-chat
examples/dockerhub/nextjs-vercel-ai-sdk-chat.md
.github/workflows/docker-publish.yml
.github/workflows/docker-auto-tag.yml
.github/scripts/auto-tag.sh
EOF
      ;;
    cli)
      cat <<'EOF'
examples/gonkagate-chat-cli
examples/dockerhub/gonkagate-chat-cli.md
.github/workflows/docker-publish.yml
.github/workflows/docker-auto-tag.yml
.github/scripts/auto-tag.sh
EOF
      ;;
    *)
      echo "Unknown target: $1" >&2
      exit 1
      ;;
  esac
}

latest_tag() {
  local prefix="$1"
  git tag -l "${prefix}*" --sort=-v:refname | head -n1
}

version_from_tag() {
  local tag="$1"
  local prefix="$2"
  echo "${tag#${prefix}}"
}

collect_target_paths() {
  local target="$1"
  mapfile -t CURRENT_TARGET_PATHS < <(target_paths "$target")
}

has_relevant_changes() {
  local target="$1"
  local last_tag="$2"

  collect_target_paths "$target"

  if [[ -z "$last_tag" ]]; then
    [[ -n "$(git rev-list -n1 HEAD -- "${CURRENT_TARGET_PATHS[@]}" || true)" ]]
    return
  fi

  if git diff --quiet "${last_tag}..HEAD" -- "${CURRENT_TARGET_PATHS[@]}"; then
    return 1
  fi

  return 0
}

resolve_bump_type() {
  local target="$1"
  local last_tag="$2"
  local range subjects bodies

  collect_target_paths "$target"

  if [[ -z "$last_tag" ]]; then
    echo "minor"
    return
  fi

  range="${last_tag}..HEAD"
  subjects="$(git log --format='%s' "$range" -- "${CURRENT_TARGET_PATHS[@]}" || true)"
  bodies="$(git log --format='%b' "$range" -- "${CURRENT_TARGET_PATHS[@]}" || true)"

  if [[ -z "$subjects" && -z "$bodies" ]]; then
    echo "none"
    return
  fi

  if grep -Eiq 'BREAKING[ -]CHANGE' <<<"$bodies"; then
    echo "major"
    return
  fi

  if grep -Eiq '^[a-zA-Z]+(\([^)]+\))?!:' <<<"$subjects"; then
    echo "major"
    return
  fi

  if grep -Eiq '^feat(\([^)]+\))?:' <<<"$subjects"; then
    echo "minor"
    return
  fi

  echo "patch"
}

create_tag() {
  local tag="$1"
  local message="$2"

  if git rev-parse -q --verify "refs/tags/${tag}" >/dev/null; then
    echo "Tag already exists, skipping: ${tag}"
    return 0
  fi

  if is_true "$DRY_RUN"; then
    PLANNED_TAGS+=("$tag")
    echo "[DRY RUN] Would create tag ${tag}"
  else
    git tag -a "$tag" -m "$message"
    CREATED_TAGS+=("$tag")
    echo "Created tag ${tag}"
  fi

  return 0
}

release_target() {
  local target="$1"
  local prefix last current_version bump next_version tag

  prefix="$(target_prefix "$target")"
  last="$(latest_tag "$prefix")"

  if ! has_relevant_changes "$target" "$last"; then
    echo "No relevant changes for ${target}; skipping."
    return
  fi

  if [[ -z "$last" ]]; then
    next_version="$BASELINE_VERSION"
    bump="minor"
  else
    current_version="$(version_from_tag "$last" "$prefix")"
    require_semver "$current_version"
    bump="$(resolve_bump_type "$target" "$last")"
    if [[ "$bump" == "none" ]]; then
      echo "No commits matched for ${target}; skipping."
      return
    fi
    next_version="$(bump_version "$current_version" "$bump")"
  fi

  tag="${prefix}${next_version}"
  create_tag "$tag" "release(${target}): ${next_version} (${bump})"
}

require_semver "$BASELINE_VERSION"
git fetch --force --tags >/dev/null 2>&1 || true

declare -a CREATED_TAGS=()
declare -a PLANNED_TAGS=()
declare -a CURRENT_TARGET_PATHS=()

release_target "nextjs"
release_target "cli"

if [[ "${#CREATED_TAGS[@]}" -eq 0 && "${#PLANNED_TAGS[@]}" -eq 0 ]]; then
  echo "No new tags were created."
  exit 0
fi

if is_true "$DRY_RUN"; then
  echo "[DRY RUN] Planned tags: ${PLANNED_TAGS[*]}"
  echo "[DRY RUN] Tag push is skipped."
  exit 0
fi

if is_true "$PUSH_TAGS"; then
  git push origin "${CREATED_TAGS[@]}"
  echo "Pushed tags: ${CREATED_TAGS[*]}"
else
  echo "Tag push disabled via PUSH_TAGS=false."
fi
