namespace GonkaGate.Examples;

internal static class CliArgs
{
    private const string SmokeFlag = "--smoke";
    private const string DefaultPrompt = "Say hi from GonkaGate in one short sentence.";

    public static CliOptions Parse(string[] args)
    {
        var isSmokeMode = args.Any(IsSmokeFlagArg);
        var prompt = string.Join(" ", args.Where(arg => !IsSmokeFlagArg(arg))).Trim();

        if (string.IsNullOrWhiteSpace(prompt))
        {
            prompt = DefaultPrompt;
        }

        return new CliOptions(isSmokeMode, prompt);
    }

    private static bool IsSmokeFlagArg(string arg)
    {
        return string.Equals(arg, SmokeFlag, StringComparison.OrdinalIgnoreCase);
    }
}
