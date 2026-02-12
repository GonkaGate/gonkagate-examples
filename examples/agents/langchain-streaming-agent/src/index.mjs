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

const multiplyNumbers = tool(
  ({ a, b }) => `Product: ${a * b}`,
  {
    name: "multiply_numbers",
    description: "Multiply two numbers and return the result.",
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
  tools: [multiplyNumbers, getUtcTime],
  systemPrompt:
    "You are a concise assistant. Use tools when needed and keep final answers brief."
});

const prompt =
  promptArgs.join(" ").trim() ||
  "What is 7 * 8? Use multiply_numbers and answer in one short sentence.";

function readTextBlocks(contentBlocks) {
  if (!Array.isArray(contentBlocks)) {
    return "";
  }

  return contentBlocks
    .filter((block) => block && typeof block === "object")
    .map((block) => {
      if (block.type === "text" && typeof block.text === "string") {
        return block.text;
      }

      return "";
    })
    .join("");
}

try {
  const stream = await agent.stream(
    {
      messages: [{ role: "user", content: prompt }]
    },
    {
      streamMode: ["updates", "messages"]
    }
  );

  let wroteStreamPrefix = false;
  let streamedAnyText = false;

  for await (const [mode, chunk] of stream) {
    if (mode === "updates") {
      const step = Object.keys(chunk ?? {})[0];
      if (step) {
        console.log(`[update] ${step}`);
      }
      continue;
    }

    if (mode === "messages" && Array.isArray(chunk)) {
      const [token, metadata] = chunk;
      if (metadata?.langgraph_node !== "model") {
        continue;
      }

      const text = readTextBlocks(token?.content_blocks ?? token?.contentBlocks);
      if (!text) {
        continue;
      }

      if (!wroteStreamPrefix) {
        process.stdout.write("Agent stream: ");
        wroteStreamPrefix = true;
      }

      process.stdout.write(text);
      streamedAnyText = true;
    }
  }

  if (streamedAnyText) {
    process.stdout.write("\n");
  }

  console.log("[stream complete]");
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
