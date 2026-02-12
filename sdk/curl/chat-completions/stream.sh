#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

API_KEY="${GONKAGATE_API_KEY:-${OPENAI_API_KEY:-}}"
BASE_URL="https://api.gonkagate.com/v1"
MODEL="${GONKAGATE_MODEL:-your_model_id_here}"
PAYLOAD_TEMPLATE="$SCRIPT_DIR/request-stream.json"

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

extract_content() {
  local chunk="$1"
  local content=""

  if command -v jq >/dev/null 2>&1; then
    content="$(printf '%s' "$chunk" | jq -r '.choices[0].delta.content // empty' 2>/dev/null || true)"
  else
    content="$(printf '%s\n' "$chunk" | sed -n 's/.*"content":"\([^"]*\)".*/\1/p')"
    content="${content//\\n/$'\n'}"
    content="${content//\\t/$'\t'}"
    content="${content//\\\"/\"}"
    content="${content//\\\\/\\}"
  fi

  printf '%s' "$content"
}

payload="$(sed "s/__MODEL__/$(escape_for_sed "$MODEL")/g" "$PAYLOAD_TEMPLATE")"
raw_stream_file="$(mktemp)"
cleanup() {
  rm -f "$raw_stream_file"
}
trap cleanup EXIT

status_code=""
saw_done=0

while IFS= read -r line || [[ -n "$line" ]]; do
  if [[ "$line" == "__CURL_HTTP_STATUS__:"* ]]; then
    status_code="${line#__CURL_HTTP_STATUS__:}"
    continue
  fi

  printf '%s\n' "$line" >>"$raw_stream_file"

  [[ "$line" == data:* ]] || continue
  chunk="${line#data: }"

  if [[ "$chunk" == "[DONE]" ]]; then
    saw_done=1
    continue
  fi

  content="$(extract_content "$chunk")"
  if [[ -n "$content" ]]; then
    printf '%s' "$content"
  fi
done < <(
  curl -sS -N \
    -X POST "$BASE_URL/chat/completions" \
    -H "Authorization: Bearer $API_KEY" \
    -H "Content-Type: application/json" \
    --data "$payload" \
    -w $'\n__CURL_HTTP_STATUS__:%{http_code}\n'
)

if [[ -z "$status_code" ]]; then
  echo >&2
  echo "Unable to determine HTTP status from stream response." >&2
  echo "Raw stream:" >&2
  cat "$raw_stream_file" >&2
  exit 1
fi

if [[ "$status_code" != 2* ]]; then
  echo >&2
  echo "Streaming request failed with HTTP $status_code" >&2
  case "$status_code" in
    401) echo "401 Unauthorized: check API key." >&2 ;;
    402) echo "402 Payment Required: check GonkaGate billing balance." >&2 ;;
    429) echo "429 Too Many Requests: retry with backoff." >&2 ;;
    503) echo "503 Service Unavailable: try again later." >&2 ;;
    *) echo "Unexpected status code returned by API." >&2 ;;
  esac
  echo "Response body:" >&2
  cat "$raw_stream_file" >&2
  exit 1
fi

if [[ "$saw_done" -eq 1 ]]; then
  printf '\n'
fi
