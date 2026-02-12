using System.Net;
using DotNetEnv;
using OpenAI;
using OpenAI.Chat;

const string DefaultBaseUrl = "https://api.gonkagate.com/v1";
const string DefaultPrompt = "Say hi from GonkaGate in one short sentence.";

TryLoadDotEnv();
WarnIfBaseUrlOverrideSet();

var isSmokeMode = args.Any(arg => string.Equals(arg, "--smoke", StringComparison.OrdinalIgnoreCase));
var prompt = string.Join(
    " ",
    args.Where(arg => !string.Equals(arg, "--smoke", StringComparison.OrdinalIgnoreCase))
).Trim();

if (string.IsNullOrWhiteSpace(prompt))
{
    prompt = DefaultPrompt;
}

var apiKey = FirstNonEmpty(
    Environment.GetEnvironmentVariable("GONKAGATE_API_KEY"),
    Environment.GetEnvironmentVariable("OPENAI_API_KEY")
);

var baseUrl = DefaultBaseUrl;

var model = FirstNonEmpty(Environment.GetEnvironmentVariable("GONKAGATE_MODEL"));

if (isSmokeMode)
{
    Console.WriteLine("Smoke check passed: script can start and parse configuration.");
    return 0;
}

if (string.IsNullOrWhiteSpace(apiKey))
{
    return Fail("Missing API key. Set GONKAGATE_API_KEY (recommended) or OPENAI_API_KEY in your environment.");
}

if (string.IsNullOrWhiteSpace(model))
{
    return Fail("Missing model. Set GONKAGATE_MODEL in your environment.");
}

try
{
    var options = new OpenAIClientOptions
    {
        Endpoint = new Uri(baseUrl)
    };

    var client = new ChatClient(model, apiKey, options);

    var completion = client.CompleteChat(
        [
            new SystemChatMessage("You are a concise assistant."),
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
        return Fail("Received a response but no message content.");
    }

    Console.WriteLine("Model response:");
    Console.WriteLine(text);
    return 0;
}
catch (Exception ex)
{
    return HandleRequestException(ex);
}

static void TryLoadDotEnv()
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

static int HandleRequestException(Exception ex)
{
    var status = GetStatusCode(ex);

    if (status == 401)
    {
        return Fail("401 Unauthorized. Check your API key.");
    }

    if (status == 402)
    {
        return Fail("402 Payment Required. Check your GonkaGate balance or billing status.");
    }

    if (status == 429)
    {
        return Fail("429 Too Many Requests. Slow down request rate and retry.");
    }

    if (status == 503)
    {
        return Fail("503 Service Unavailable. Retry in a few seconds.");
    }

    var message = GetErrorMessage(ex);
    return Fail(message);
}

static void WarnIfBaseUrlOverrideSet()
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

static int? GetStatusCode(Exception ex)
{
    for (Exception? current = ex; current is not null; current = current.InnerException)
    {
        if (TryReadStatusCode(current, out var status))
        {
            return status;
        }

        if (TryReadResponseStatusCode(current, out status))
        {
            return status;
        }
    }

    return null;
}

static bool TryReadStatusCode(object source, out int statusCode)
{
    foreach (var propertyName in new[] { "StatusCode", "Status" })
    {
        if (TryReadIntProperty(source, propertyName, out statusCode))
        {
            return true;
        }
    }

    statusCode = default;
    return false;
}

static bool TryReadResponseStatusCode(object source, out int statusCode)
{
    var responseProperty = source.GetType().GetProperty("Response");
    var response = responseProperty?.GetValue(source);

    if (response is null)
    {
        statusCode = default;
        return false;
    }

    return TryReadStatusCode(response, out statusCode);
}

static bool TryReadIntProperty(object source, string propertyName, out int value)
{
    var property = source.GetType().GetProperty(propertyName);
    var rawValue = property?.GetValue(source);

    if (rawValue is int intValue)
    {
        value = intValue;
        return true;
    }

    if (rawValue is HttpStatusCode httpStatusCode)
    {
        value = (int)httpStatusCode;
        return true;
    }

    value = default;
    return false;
}

static string GetErrorMessage(Exception ex)
{
    for (Exception? current = ex; current is not null; current = current.InnerException)
    {
        var property = current.GetType().GetProperty("Message");
        var rawValue = property?.GetValue(current) as string;
        if (!string.IsNullOrWhiteSpace(rawValue))
        {
            return rawValue.Trim();
        }
    }

    return "Unknown request error.";
}

static string FirstNonEmpty(params string?[] values)
{
    foreach (var value in values)
    {
        if (!string.IsNullOrWhiteSpace(value))
        {
            return value.Trim();
        }
    }

    return string.Empty;
}

static int Fail(string message)
{
    Console.Error.WriteLine($"Error: {message}");
    return 1;
}
