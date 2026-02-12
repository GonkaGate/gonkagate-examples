import { createOpenAICompatible } from "@ai-sdk/openai-compatible";
import { convertToModelMessages, streamText, type UIMessage } from "ai";

export const maxDuration = 30;

const DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";
const hasBaseURLOverride =
  Boolean(process.env.GONKAGATE_BASE_URL) || Boolean(process.env.OPENAI_BASE_URL);

if (hasBaseURLOverride) {
  console.warn(
    `Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to ${DEFAULT_BASE_URL}.`,
  );
}

function getStatusCode(error: unknown): number | undefined {
  if (typeof error !== "object" || error === null) {
    return undefined;
  }

  const maybeError = error as {
    statusCode?: unknown;
    response?: { status?: unknown };
  };

  if (typeof maybeError.statusCode === "number") {
    return maybeError.statusCode;
  }

  if (typeof maybeError.response?.status === "number") {
    return maybeError.response.status;
  }

  return undefined;
}

function mapErrorMessage(statusCode: number | undefined): string {
  switch (statusCode) {
    case 401:
      return "Unauthorized (401): invalid API key.";
    case 402:
      return "Payment required (402): check GonkaGate billing/balance.";
    case 429:
      return "Rate limited (429): too many requests, try again shortly.";
    case 503:
      return "Service unavailable (503): temporary upstream issue, retry later.";
    default:
      return "Request failed. Check server logs and environment variables.";
  }
}

export async function POST(req: Request) {
  const apiKey = process.env.GONKAGATE_API_KEY ?? process.env.OPENAI_API_KEY;
  const baseURL = DEFAULT_BASE_URL;
  const model = process.env.GONKAGATE_MODEL ?? process.env.OPENAI_MODEL;

  if (!apiKey) {
    return Response.json(
      {
        error:
          "Missing API key: set GONKAGATE_API_KEY (or OPENAI_API_KEY fallback).",
      },
      { status: 500 },
    );
  }

  if (!model) {
    return Response.json(
      {
        error:
          "Missing model: set GONKAGATE_MODEL (or OPENAI_MODEL fallback).",
      },
      { status: 400 },
    );
  }

  try {
    const { messages }: { messages: UIMessage[] } = await req.json();

    const provider = createOpenAICompatible({
      name: "gonkagate",
      apiKey,
      baseURL,
    });

    const result = streamText({
      model: provider.chatModel(model),
      system:
        "You are a concise and helpful assistant. Keep answers direct and practical.",
      messages: await convertToModelMessages(messages),
    });

    return result.toUIMessageStreamResponse();
  } catch (error) {
    const statusCode = getStatusCode(error);

    return Response.json(
      {
        error: mapErrorMessage(statusCode),
      },
      { status: statusCode ?? 500 },
    );
  }
}
