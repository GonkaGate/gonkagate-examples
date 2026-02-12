#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

API_KEY="${GONKAGATE_API_KEY:-${OPENAI_API_KEY:-}}"
BASE_URL="https://api.gonkagate.com/v1"
MODEL="${GONKAGATE_MODEL:-your_model_id_here}"
PAYLOAD_TEMPLATE="$SCRIPT_DIR/request.json"

if [[ -n "${GONKAGATE_BASE_URL:-}" || -n "${OPENAI_BASE_URL:-}" ]]; then
  echo "Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to $BASE_URL." >&2
fi

if [[ -z "$API_KEY" ]]; then
  echo "Missing API key. Set GONKAGATE_API_KEY or OPENAI_API_KEY." >&2
  exit 1
fi

if [[ "$MODEL" == "your_model_id_here" ]]; then
  echo "Missing model. Set GONKAGATE_MODEL to a valid model id." >&2
  exit 1
fi

if [[ ! -f "$PAYLOAD_TEMPLATE" ]]; then
  echo "Missing payload file: $PAYLOAD_TEMPLATE" >&2
  exit 1
fi

BASE_URL="${BASE_URL%/}"

escape_for_sed() {
  printf '%s' "$1" | sed 's/[\/&]/\\&/g'
}

payload="$(sed "s/__MODEL__/$(escape_for_sed "$MODEL")/g" "$PAYLOAD_TEMPLATE")"
response_file="$(mktemp)"
cleanup() {
  rm -f "$response_file"
}
trap cleanup EXIT

status_code="$(
  curl -sS \
    -X POST "$BASE_URL/chat/completions" \
    -H "Authorization: Bearer $API_KEY" \
    -H "Content-Type: application/json" \
    --data "$payload" \
    -o "$response_file" \
    -w "%{http_code}"
)"

if [[ "$status_code" == 2* ]]; then
  if command -v jq >/dev/null 2>&1; then
    jq . "$response_file" 2>/dev/null || cat "$response_file"
  else
    cat "$response_file"
  fi
  printf '\n'
  exit 0
fi

echo "Request failed with HTTP $status_code" >&2
case "$status_code" in
  401) echo "401 Unauthorized: check API key." >&2 ;;
  402) echo "402 Payment Required: check GonkaGate billing balance." >&2 ;;
  429) echo "429 Too Many Requests: retry with backoff." >&2 ;;
  503) echo "503 Service Unavailable: try again later." >&2 ;;
  *) echo "Unexpected status code returned by API." >&2 ;;
esac

echo "Response body:" >&2
cat "$response_file" >&2
exit 1
