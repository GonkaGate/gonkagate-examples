using OpenAI;
using OpenAI.Chat;

namespace GonkaGate.Examples;

internal static class ChatService
{
    private const string SystemPrompt = "You are a concise assistant.";

    public static string Complete(AppConfig config, string prompt)
    {
        var options = new OpenAIClientOptions
        {
            Endpoint = new Uri(config.BaseUrl)
        };

        var client = new ChatClient(config.Model, config.ApiKey, options);

        var completion = client.CompleteChat(
            [
                new SystemChatMessage(SystemPrompt),
                new UserChatMessage(prompt)
            ],
            new ChatCompletionOptions
            {
                Temperature = 0.2f
            }
        );

        var text = string.Concat(
            completion.Content
                .Where(contentPart => !string.IsNullOrWhiteSpace(contentPart.Text))
                .Select(contentPart => contentPart.Text!.Trim())
        );

        if (string.IsNullOrWhiteSpace(text))
        {
            throw new InvalidOperationException("Received a response but no message content.");
        }

        return text;
    }
}
