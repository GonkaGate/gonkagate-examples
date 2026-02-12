"use client";

import { useState } from "react";
import { useChat } from "@ai-sdk/react";
import { Loader2, Send } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { cn } from "@/lib/utils";

function getMessageText(message: { parts: Array<{ type: string; text?: string }> }) {
  return message.parts
    .filter((part) => part.type === "text")
    .map((part) => part.text ?? "")
    .join("")
    .trim();
}

export default function Home() {
  const [input, setInput] = useState("");
  const { messages, sendMessage, status, error } = useChat();

  const isSending = status === "submitted" || status === "streaming";

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-3xl items-center px-4 py-8">
      <Card className="w-full">
        <CardHeader>
          <CardTitle>GonkaGate + Vercel AI SDK Chat</CardTitle>
          <CardDescription>
            Minimal Next.js chat UI with streaming responses from an OpenAI-compatible
            endpoint.
          </CardDescription>
        </CardHeader>

        <CardContent className="space-y-4">
          <div className="flex h-[420px] flex-col gap-3 overflow-y-auto rounded-md border border-border bg-muted/30 p-3">
            {messages.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                Start the chat by sending a message.
              </p>
            ) : null}

            {messages.map((message) => {
              const text = getMessageText(message);

              return (
                <div
                  key={message.id}
                  className={cn(
                    "max-w-[90%] rounded-md px-3 py-2 text-sm whitespace-pre-wrap",
                    message.role === "user"
                      ? "self-end bg-primary text-primary-foreground"
                      : "self-start border border-border bg-background",
                  )}
                >
                  {text || "..."}
                </div>
              );
            })}
          </div>

          <form
            className="space-y-3"
            onSubmit={(event) => {
              event.preventDefault();

              const value = input.trim();
              if (!value || isSending) {
                return;
              }

              sendMessage({ text: value });
              setInput("");
            }}
          >
            <Textarea
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="Ask something..."
              rows={4}
            />

            <div className="flex items-center justify-between gap-2">
              <p className="text-xs text-muted-foreground">
                Status: {status}
              </p>

              <Button type="submit" disabled={isSending || input.trim().length === 0}>
                {isSending ? <Loader2 className="size-4 animate-spin" /> : <Send className="size-4" />}
                Send
              </Button>
            </div>
          </form>

          {error ? (
            <p className="rounded-md border border-red-300 bg-red-50 px-3 py-2 text-sm text-red-700">
              {error.message}
            </p>
          ) : null}
        </CardContent>
      </Card>
    </main>
  );
}
