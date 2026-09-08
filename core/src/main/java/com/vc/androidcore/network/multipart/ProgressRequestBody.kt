package com.vc.androidcore.network.multipart

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import java.io.IOException

/**
 * Functional interface / callback for upload progress updates.
 *
 * @param bytesWritten Number of bytes successfully transferred so far.
 * @param totalBytes Total size of the payload in bytes (or -1 if unknown).
 * @param percentage Completion percentage from 0 to 100 (or -1 if total is unknown).
 */
typealias UploadProgressListener = (bytesWritten: Long, totalBytes: Long, percentage: Int) -> Unit

/**
 * Custom [RequestBody] decorator that wraps any standard [RequestBody]
 * and reports write progress to the provided [UploadProgressListener] on the main thread.
 */
class ProgressRequestBody(
    private val delegate: RequestBody,
    private val onProgress: UploadProgressListener?
) : RequestBody() {

    private val mainHandler: Handler? by lazy {
        runCatching { Handler(Looper.getMainLooper()) }.getOrNull()
    }

    override fun contentType(): MediaType? = delegate.contentType()

    @Throws(IOException::class)
    override fun contentLength(): Long = delegate.contentLength()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (onProgress == null) {
            delegate.writeTo(sink)
            return
        }

        val countingSink = CountingSink(sink, contentLength(), onProgress, mainHandler)
        val bufferedSink = countingSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    private class CountingSink(
        delegate: Sink,
        private val totalBytes: Long,
        private val onProgress: UploadProgressListener,
        private val handler: Handler?
    ) : ForwardingSink(delegate) {

        private var bytesWritten: Long = 0L
        private var lastReportedPercent: Int = -1

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount

            val percent = if (totalBytes > 0L) {
                ((bytesWritten * 100) / totalBytes).toInt().coerceIn(0, 100)
            } else {
                -1
            }

            // Avoid flooding the UI with identical percentage updates
            if (percent != lastReportedPercent || bytesWritten == totalBytes) {
                lastReportedPercent = percent
                if (handler != null) {
                    handler.post {
                        onProgress(bytesWritten, totalBytes, percent)
                    }
                } else {
                    onProgress(bytesWritten, totalBytes, percent)
                }
            }
        }
    }
}
