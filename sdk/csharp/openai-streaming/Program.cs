namespace GonkaGate.Examples;

internal static class Program
{
    public static int Main(string[] args)
    {
        DotEnvLoader.TryLoad();

        var config = ConfigLoader.Load();
        var cliOptions = CliArgs.Parse(args);

        if (cliOptions.IsSmokeMode)
        {
            Console.WriteLine("Smoke check passed: config is valid and script can start.");
            return 0;
        }

        if (StringUtils.IsBlank(config.ApiKey))
        {
            return ErrorHandler.Fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (StringUtils.IsBlank(config.Model) || string.Equals(config.Model, ConfigLoader.DefaultModel, StringComparison.Ordinal))
        {
            return ErrorHandler.Fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        try
        {
            StreamingChatService.Stream(config, cliOptions.Prompt);
            return 0;
        }
        catch (Exception ex)
        {
            return ErrorHandler.HandleRequestException(ex);
        }
    }
}
