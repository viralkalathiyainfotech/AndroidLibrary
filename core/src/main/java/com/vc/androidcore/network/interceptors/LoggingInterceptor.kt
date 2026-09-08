package com.vc.androidcore.network.interceptors

import com.vc.androidcore.config.CoreLibrary
import com.vc.androidcore.logging.CoreLogger
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Production-ready OkHttp logging interceptor that automatically redacts sensitive headers,
 * tokens, and request/response body fields.
 */
class LoggingInterceptor(
    private val tag: String = "CoreNetwork"
) : Interceptor {

    private val sensitiveHeaderNames = setOf(
        "authorization",
        "proxy-authorization",
        "cookie",
        "set-cookie",
        "x-api-key",
        "token"
    )

    private val sensitiveBodyPatterns = listOf(
        Regex("(?i)(\"password\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"token\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"access_token\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"refresh_token\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"secret\"\\s*:\\s*\")([^\"]+)(\")")
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!CoreLibrary.config.enableNetworkLogging) {
            return chain.proceed(request)
        }

        // Log Request
        val requestBody = request.body
        val hasRequestBody = requestBody != null

        CoreLogger.d("--> ${request.method} ${request.url}", tag)
        logHeaders(request.headers)

        if (hasRequestBody && !isGzip(request.headers)) {
            val buffer = Buffer()
            requestBody?.writeTo(buffer)
            val contentType = requestBody?.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            val bodyString = buffer.readString(charset)
            CoreLogger.d("Body: ${maskBody(bodyString)}", tag)
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            CoreLogger.e("<-- HTTP FAILED: $e", tag)
            throw e
        }

        val tookMs = (System.nanoTime() - startNs) / 1e6
        CoreLogger.d("<-- ${response.code} ${response.message} ${response.request.url} (${tookMs}ms)", tag)
        logHeaders(response.headers)

        val responseBody = response.body
        if (responseBody != null && !isGzip(response.headers)) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer.clone()
            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            val bodyString = buffer.readString(charset)
            CoreLogger.d("Response: ${maskBody(bodyString)}", tag)
        }
        CoreLogger.d("<-- END HTTP", tag)

        return response
    }

    private fun logHeaders(headers: Headers) {
        for (i in 0 until headers.size) {
            val name = headers.name(i)
            val value = if (sensitiveHeaderNames.contains(name.lowercase())) {
                "[REDACTED]"
            } else {
                headers.value(i)
            }
            CoreLogger.d("$name: $value", tag)
        }
    }

    private fun maskBody(body: String): String {
        var masked = body
        for (pattern in sensitiveBodyPatterns) {
            masked = pattern.replace(masked) { matchResult ->
                val p1 = matchResult.groupValues[1]
                val p3 = matchResult.groupValues[3]
                "$p1[REDACTED]$p3"
            }
        }
        return masked
    }

    private fun isGzip(headers: Headers): Boolean {
        return headers.get("Content-Encoding")?.equals("gzip", ignoreCase = true) == true
    }
}
