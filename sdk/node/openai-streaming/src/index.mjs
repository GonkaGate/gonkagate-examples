import "dotenv/config";
import OpenAI from "openai";

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
  model: process.env.GONKAGATE_MODEL ?? "your_model"
};

function fail(message) {
  console.error(`Error: ${message}`);
  process.exit(1);
}

if (!config.apiKey) {
  fail(
    "Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment."
  );
}

if (!config.model || config.model === "your_model") {
  fail("Missing model. Set GONKAGATE_MODEL in your environment.");
}

if (isSmokeMode) {
  console.log("Smoke check passed: config is valid and script can start.");
  process.exit(0);
}

const prompt =
  promptArgs.join(" ").trim() || "Say hi from GonkaGate in one short sentence.";

const client = new OpenAI({
  apiKey: config.apiKey,
  baseURL: config.baseURL
});

try {
  const stream = await client.chat.completions.create({
    model: config.model,
    stream: true,
    temperature: 0.2,
    messages: [
      {
        role: "system",
        content: "You are a concise assistant."
      },
      {
        role: "user",
        content: prompt
      }
    ]
  });

  let emittedContent = false;

  for await (const chunk of stream) {
    const token = chunk.choices?.[0]?.delta?.content;
    if (typeof token === "string" && token.length > 0) {
      process.stdout.write(token);
      emittedContent = true;
    }
  }

  if (emittedContent) {
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
