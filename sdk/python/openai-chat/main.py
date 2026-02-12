import os
import sys

DEFAULT_BASE_URL = "https://api.gonkagate.com/v1"

try:
    from dotenv import load_dotenv
except ImportError:
    # Allow --smoke mode to run even if dependencies are not installed yet.
    def load_dotenv() -> None:
        return None


def fail(message: str) -> None:
    print(f"Error: {message}", file=sys.stderr)
    raise SystemExit(1)


def get_status_code(error: Exception) -> int | None:
    status_code = getattr(error, "status_code", None)
    if isinstance(status_code, int):
        return status_code

    status = getattr(error, "status", None)
    if isinstance(status, int):
        return status

    response = getattr(error, "response", None)
    if response is None:
        return None

    response_status_code = getattr(response, "status_code", None)
    if isinstance(response_status_code, int):
        return response_status_code

    response_status = getattr(response, "status", None)
    if isinstance(response_status, int):
        return response_status

    return None


def main() -> None:
    load_dotenv()
    is_smoke_mode = "--smoke" in sys.argv

    if os.getenv("GONKAGATE_BASE_URL") or os.getenv("OPENAI_BASE_URL"):
        print(
            f"Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to {DEFAULT_BASE_URL}.",
            file=sys.stderr,
        )

    config = {
        "api_key": os.getenv("GONKAGATE_API_KEY") or os.getenv("OPENAI_API_KEY"),
        "base_url": DEFAULT_BASE_URL,
        "model": os.getenv("GONKAGATE_MODEL"),
    }

    if is_smoke_mode:
        print("Smoke check passed: script can start and parse configuration.")
        return

    if not config["api_key"]:
        fail(
            "Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment."
        )

    if not config["model"]:
        fail("Missing model. Set GONKAGATE_MODEL in your environment.")

    try:
        from openai import OpenAI
    except ImportError:
        fail("Missing dependency 'openai'. Run: pip install -r requirements.txt")

    client = OpenAI(api_key=config["api_key"], base_url=config["base_url"])

    try:
        response = client.chat.completions.create(
            model=config["model"],
            messages=[
                {"role": "system", "content": "You are a concise assistant."},
                {
                    "role": "user",
                    "content": "Say hi from GonkaGate in one short sentence.",
                },
            ],
        )
    except Exception as error:
        status_code = get_status_code(error)

        if status_code == 401:
            fail("401 Unauthorized. Check your API key.")

        if status_code == 402:
            fail("402 Payment Required. Check your GonkaGate balance or billing status.")

        if status_code == 429:
            fail("429 Too Many Requests. Slow down request rate and retry.")

        if status_code == 503:
            fail("503 Service Unavailable. Retry in a few seconds.")

        message = str(error).strip() or "Unknown request error."
        fail(message)

    text = None
    if response.choices and response.choices[0].message:
        text = response.choices[0].message.content

    if not text:
        fail("Received a response but no message content in choices[0].")

    print("Model response:")
    print(text)


if __name__ == "__main__":
    main()
