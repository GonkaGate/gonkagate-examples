#!/usr/bin/env python3
import argparse
import os
import sys

from dotenv import load_dotenv
from openai import OpenAI

DEFAULT_BASE_URL = "https://api.gonkagate.com/v1"


def fail(message: str) -> None:
    print(f"Error: {message}", file=sys.stderr)
    raise SystemExit(1)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Minimal OpenAI SDK streaming chat completion example for GonkaGate."
    )
    parser.add_argument(
        "--smoke",
        action="store_true",
        help="Run startup validation without making a network request.",
    )
    parser.add_argument(
        "prompt",
        nargs="*",
        help="Optional prompt text. If omitted, a default prompt is used.",
    )
    return parser.parse_args()


def get_status_code(error: Exception) -> int | None:
    status = getattr(error, "status_code", None)
    if isinstance(status, int):
        return status

    response = getattr(error, "response", None)
    for attr in ("status_code", "status"):
        value = getattr(response, attr, None)
        if isinstance(value, int):
            return value
    return None


def get_error_message(error: Exception) -> str:
    body = getattr(error, "body", None)
    if isinstance(body, dict):
        body_error = body.get("error")
        if isinstance(body_error, dict):
            message = body_error.get("message")
            if isinstance(message, str) and message.strip():
                return message.strip()
        message = body.get("message")
        if isinstance(message, str) and message.strip():
            return message.strip()

    message = getattr(error, "message", None)
    if isinstance(message, str) and message.strip():
        return message.strip()

    text = str(error).strip()
    return text or "Unknown request error."


def main() -> None:
    load_dotenv()
    args = parse_args()

    if os.getenv("GONKAGATE_BASE_URL") or os.getenv("OPENAI_BASE_URL"):
        print(
            f"Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to {DEFAULT_BASE_URL}.",
            file=sys.stderr,
        )

    config = {
        "api_key": os.getenv("GONKAGATE_API_KEY") or os.getenv("OPENAI_API_KEY"),
        "base_url": DEFAULT_BASE_URL,
        "model": os.getenv("GONKAGATE_MODEL") or "your_model",
    }

    if args.smoke:
        print("Smoke check passed: config is valid and script can start.")
        return

    if not config["api_key"]:
        fail(
            "Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment."
        )

    if not config["model"] or config["model"] == "your_model":
        fail("Missing model. Set GONKAGATE_MODEL in your environment.")

    prompt = " ".join(args.prompt).strip() or "Say hi from GonkaGate in one short sentence."

    client = OpenAI(api_key=config["api_key"], base_url=config["base_url"])

    try:
        stream = client.chat.completions.create(
            model=config["model"],
            stream=True,
            temperature=0.2,
            messages=[
                {"role": "system", "content": "You are a concise assistant."},
                {"role": "user", "content": prompt},
            ],
        )

        emitted_content = False
        for chunk in stream:
            choices = getattr(chunk, "choices", None) or []
            if not choices:
                continue
            delta = getattr(choices[0], "delta", None)
            token = getattr(delta, "content", None)
            if isinstance(token, str) and token:
                sys.stdout.write(token)
                sys.stdout.flush()
                emitted_content = True

        if emitted_content:
            sys.stdout.write("\n")

        print("[stream complete]")
    except Exception as error:
        status = get_status_code(error)

        if status == 401:
            fail("401 Unauthorized. Check your API key.")
        if status == 402:
            fail("402 Payment Required. Check your GonkaGate balance or billing status.")
        if status == 429:
            fail("429 Too Many Requests. Slow down request rate and retry.")
        if status == 503:
            fail("503 Service Unavailable. Retry in a few seconds.")

        fail(get_error_message(error))


if __name__ == "__main__":
    main()
