using System.Net;

namespace GonkaGate.Examples;

internal static class ErrorHandler
{
    public static int HandleRequestException(Exception ex)
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

    public static int Fail(string message)
    {
        Console.Error.WriteLine($"Error: {message}");
        return 1;
    }

    private static int? GetStatusCode(Exception ex)
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

    private static bool TryReadStatusCode(object source, out int statusCode)
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

    private static bool TryReadResponseStatusCode(object source, out int statusCode)
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

    private static bool TryReadIntProperty(object source, string propertyName, out int value)
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

    private static string GetErrorMessage(Exception ex)
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
}
