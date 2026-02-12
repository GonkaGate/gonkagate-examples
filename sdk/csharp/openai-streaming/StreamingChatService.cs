using OpenAI;
using OpenAI.Chat;

namespace GonkaGate.Examples;

internal static class StreamingChatService
{
    private const string SystemPrompt = "You are a concise assistant.";

    public static void Stream(AppConfig config, string prompt)
    {
        var options = new OpenAIClientOptions
        {
            Endpoint = new Uri(config.BaseUrl)
        };

        var client = new ChatClient(config.Model, config.ApiKey, options);

        var updates = client.CompleteChatStreaming(
            [
                new SystemChatMessage(SystemPrompt),
                new UserChatMessage(prompt)
            ],
            new ChatCompletionOptions
            {
                Temperature = 0.2f
            }
        );

        var emittedContent = false;

        foreach (var update in updates)
        {
            foreach (var contentPart in update.ContentUpdate)
            {
                if (string.IsNullOrEmpty(contentPart.Text))
                {
                    continue;
                }

                Console.Write(contentPart.Text);
                emittedContent = true;
            }
        }

        if (emittedContent)
        {
            Console.WriteLine();
        }

        Console.WriteLine("[stream complete]");
    }
}
