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
            Console.WriteLine("Smoke check passed: script can start and parse configuration.");
            return 0;
        }

        if (StringUtils.IsBlank(config.ApiKey))
        {
            return ErrorHandler.Fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
        }

        if (StringUtils.IsBlank(config.Model))
        {
            return ErrorHandler.Fail("Missing model. Set GONKAGATE_MODEL in your environment.");
        }

        try
        {
            var text = ChatService.Complete(config, cliOptions.Prompt);
            Console.WriteLine("Model response:");
            Console.WriteLine(text);
            return 0;
        }
        catch (Exception ex)
        {
            return ErrorHandler.HandleRequestException(ex);
        }
    }
}
