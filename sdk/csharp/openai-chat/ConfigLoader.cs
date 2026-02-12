namespace GonkaGate.Examples;

internal static class ConfigLoader
{
    private const string DefaultBaseUrl = "https://api.gonkagate.com/v1";

    public static AppConfig Load()
    {
        WarnIfBaseUrlOverrideSet();

        var apiKey = StringUtils.FirstNonEmpty(
            Environment.GetEnvironmentVariable("GONKAGATE_API_KEY"),
            Environment.GetEnvironmentVariable("OPENAI_API_KEY")
        );

        var model = StringUtils.FirstNonEmpty(Environment.GetEnvironmentVariable("GONKAGATE_MODEL"));

        return new AppConfig(apiKey, DefaultBaseUrl, model);
    }

    private static void WarnIfBaseUrlOverrideSet()
    {
        var gonkagateBaseUrl = Environment.GetEnvironmentVariable("GONKAGATE_BASE_URL");
        var openaiBaseUrl = Environment.GetEnvironmentVariable("OPENAI_BASE_URL");

        if (string.IsNullOrWhiteSpace(gonkagateBaseUrl) && string.IsNullOrWhiteSpace(openaiBaseUrl))
        {
            return;
        }

        Console.Error.WriteLine(
            $"Warning: GONKAGATE_BASE_URL and OPENAI_BASE_URL are ignored. Base URL is fixed to {DefaultBaseUrl}."
        );
    }
}
