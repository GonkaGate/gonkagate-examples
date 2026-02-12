import "dotenv/config";
import * as z from "zod";
import { ChatOpenAI } from "@langchain/openai";
import { createAgent } from "langchain";

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

const extractionSchema = z.object({
  name: z.string().describe("Person name, or 'unknown' if missing"),
  email: z.string().describe("Email address, or 'unknown' if missing"),
  company: z.string().describe("Company name, or 'unknown' if missing"),
  urgency: z
    .enum(["low", "medium", "high"])
    .describe("Urgency level inferred from the text"),
  summary: z.string().describe("One-sentence summary, max 140 characters")
});

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
  tools: [],
  responseFormat: extractionSchema,
  systemPrompt:
    "Extract CRM lead fields from user text. If a field is missing, set it to 'unknown'. Keep summary under 140 characters."
});

const prompt =
  promptArgs.join(" ").trim() ||
  "Extract lead data from: Hi, I'm Alice Johnson from Acme Labs. Reach me at alice@acme.dev. We need onboarding this week.";

try {
  const result = await agent.invoke({
    messages: [{ role: "user", content: prompt }]
  });

  const structured = result?.structuredResponse;

  if (!structured || typeof structured !== "object") {
    fail("Received a response but no structuredResponse payload.");
  }

  console.log("Structured response:");
  console.log(JSON.stringify(structured, null, 2));
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

  if (error?.name === "StructuredOutputParsingError") {
    fail("Model returned structured output that did not match the schema.");
  }

  const message = error?.error?.message ?? error?.message ?? "Unknown request error.";
  fail(message);
}
