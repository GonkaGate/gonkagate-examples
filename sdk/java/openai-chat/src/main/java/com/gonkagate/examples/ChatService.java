package com.gonkagate.examples;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

final class ChatService {
    private ChatService() {
    }

    static String complete(AppConfig config, String systemPrompt, String userPrompt) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(config.apiKey())
            .baseUrl(config.baseUrl())
            .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(config.model())
            .addSystemMessage(systemPrompt)
            .addUserMessage(userPrompt)
            .build();

        ChatCompletion response = client.chat().completions().create(request);
        String text = extractText(response);

        if (Strings.isBlank(text)) {
            throw new IllegalStateException("Received a response but no message content in choices[0].");
        }

        return text;
    }

    private static String extractText(ChatCompletion response) {
        return response.choices().stream()
            .findFirst()
            .flatMap(choice -> choice.message().content())
            .map(String::trim)
            .orElse("");
    }
}
