using DotNetEnv;

namespace GonkaGate.Examples;

internal static class DotEnvLoader
{
    public static void TryLoad()
    {
        try
        {
            Env.Load();
        }
        catch
        {
            // Ignore missing or unreadable .env files; process env vars still work.
        }
    }
}
