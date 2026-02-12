package com.gonkagate.examples;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import java.util.List;
import java.util.stream.Stream;

public final class StreamingChatRunner {
    public void run(AppConfig config, String prompt) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(config.apiKey())
            .baseUrl(config.baseUrl())
            .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(config.model())
            .addSystemMessage("You are a concise assistant.")
            .addUserMessage(prompt)
            .temperature(0.2)
            .build();

        try (
            StreamResponse<ChatCompletionChunk> streamResponse = client.chat().completions().createStreaming(request);
            Stream<ChatCompletionChunk> chunks = streamResponse.stream()
        ) {
            boolean emittedContent = false;
            var iterator = chunks.iterator();

            while (iterator.hasNext()) {
                ChatCompletionChunk chunk = iterator.next();
                String token = extractToken(chunk);
                if (token.isEmpty()) {
                    continue;
                }

                System.out.print(token);
                emittedContent = true;
            }

            if (emittedContent) {
                System.out.println();
            }

            System.out.println("[stream complete]");
        }
    }

    private static String extractToken(ChatCompletionChunk chunk) {
        List<ChatCompletionChunk.Choice> choices = chunk.choices();
        if (choices.isEmpty()) {
            return "";
        }

        return choices.get(0).delta().content().orElse("");
    }
}
