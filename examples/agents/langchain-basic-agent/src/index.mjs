import "dotenv/config";
import * as z from "zod";
import { ChatOpenAI } from "@langchain/openai";
import { createAgent, tool } from "langchain";

const isSmokeMode = process.argv.includes("--smoke");
const promptArgs = process.argv.slice(2).filter((arg) => arg !== "--smoke");
const DEFAULT_BASE_URL = "https://api.gonkagate.com/v1";

if (process.env.GONKAGATE_BASE_URL || process.env.OPENAI_BASE_URL) {
  console.warn(
    `Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to ${DEFAULT_BASE_URL}.`
  );
}

const config = {
  apiKey: process.env.GONKAGATE_API_KEY ?? process.env.OPENAI_API_KEY,
  baseURL: DEFAULT_BASE_URL,
  model: process.env.GONKAGATE_MODEL
};

function fail(message) {
  console.error(`Error: ${message}`);
  process.exit(1);
}

if (isSmokeMode) {
  console.log("Smoke check passed: script can start and parse configuration.");
  process.exit(0);
}

if (!config.apiKey) {
  fail(
    "Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment."
  );
}

if (!config.model || config.model === "your_model") {
  fail("Missing model. Set GONKAGATE_MODEL in your environment.");
}

const addNumbers = tool(
  ({ a, b }) => {
    const sum = a + b;
    return `Sum: ${sum}`;
  },
  {
    name: "add_numbers",
    description: "Add two numbers and return the sum.",
    schema: z.object({
      a: z.number().describe("The first number"),
      b: z.number().describe("The second number")
    })
  }
);

const getUtcTime = tool(
  () => `UTC time: ${new Date().toISOString()}`,
  {
    name: "get_utc_time",
    description: "Get the current UTC timestamp.",
    schema: z.object({})
  }
);

const model = new ChatOpenAI({
  apiKey: config.apiKey,
  model: config.model,
  temperature: 0,
  configuration: {
    baseURL: config.baseURL
  }
});

const agent = createAgent({
  model,
  tools: [addNumbers, getUtcTime],
  systemPrompt:
    "You are a concise assistant. Use tools when needed and provide the final answer clearly."
});

const prompt =
  promptArgs.join(" ").trim() ||
  "What is 21 + 21? Use the add_numbers tool and answer in one short sentence.";

function normalizeContent(content) {
  if (typeof content === "string" && content.trim()) {
    return content.trim();
  }

  if (Array.isArray(content)) {
    const textParts = content
      .map((part) => {
        if (typeof part === "string") {
          return part;
        }

        if (part && typeof part === "object" && "text" in part) {
          return part.text;
        }

        return "";
      })
      .filter((value) => typeof value === "string" && value.trim().length > 0);

    if (textParts.length > 0) {
      return textParts.join("\n").trim();
    }
  }

  return null;
}

function getFinalText(result) {
  const direct = normalizeContent(result?.content);
  if (direct) {
    return direct;
  }

  const messages = result?.messages;
  if (Array.isArray(messages) && messages.length > 0) {
    const lastMessage = messages[messages.length - 1];
    const fromLastMessage = normalizeContent(lastMessage?.content);
    if (fromLastMessage) {
      return fromLastMessage;
    }
  }

  return null;
}

try {
  const result = await agent.invoke({
    messages: [{ role: "user", content: prompt }]
  });

  const text = getFinalText(result);
  if (!text) {
    fail("Received a response but could not parse final agent text.");
  }

  console.log("Agent response:");
  console.log(text);
} catch (error) {
  const status = error?.status ?? error?.response?.status;

  if (status === 401) {
    fail("401 Unauthorized. Check your API key.");
  }

  if (status === 402) {
    fail("402 Payment Required. Check your GonkaGate balance or billing status.");
  }

  if (status === 429) {
    fail("429 Too Many Requests. Slow down request rate and retry.");
  }

  if (status === 503) {
    fail("503 Service Unavailable. Retry in a few seconds.");
  }

  const message = error?.error?.message ?? error?.message ?? "Unknown request error.";
  fail(message);
}
