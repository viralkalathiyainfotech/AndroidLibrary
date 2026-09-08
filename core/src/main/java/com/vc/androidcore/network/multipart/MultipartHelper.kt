package com.vc.androidcore.network.multipart

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Enterprise utility helper for constructing OkHttp & Retrofit [MultipartBody.Part] instances
 * from [File], [Uri], or [ByteArray], supporting auto MIME-type detection and upload progress.
 */
object MultipartHelper {

    private const val DEFAULT_MIME_TYPE = "application/octet-stream"

    /**
     * Constructs a [MultipartBody.Part] directly from a local [File].
     *
     * @param file The file to upload.
     * @param partName The form-data key name (e.g., "file", "avatar", "document").
     * @param customFileName Optional override for the file name sent to the server.
     * @param onProgress Optional callback receiving upload progress (bytesWritten, totalBytes, percentage).
     */
    fun createPartFromFile(
        file: File,
        partName: String = "file",
        customFileName: String? = null,
        onProgress: UploadProgressListener? = null
    ): MultipartBody.Part {
        val mimeType = getMimeTypeFromFile(file)
        val rawBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val body = if (onProgress != null) ProgressRequestBody(rawBody, onProgress) else rawBody
        val fileName = customFileName ?: file.name

        return MultipartBody.Part.createFormData(partName, fileName, body)
    }

    /**
     * Constructs a [MultipartBody.Part] from an Android content [Uri].
     * Copies the content to a temporary cache file to ensure streaming safety and accurate byte counting.
     *
     * @param context Application or Activity context.
     * @param uri The content [Uri] selected by the user (e.g. from PhotoPicker or Gallery).
     * @param partName The form-data key name.
     * @param customFileName Optional override for the file name.
     * @param onProgress Optional callback receiving upload progress.
     */
    fun createPartFromUri(
        context: Context,
        uri: Uri,
        partName: String = "file",
        customFileName: String? = null,
        onProgress: UploadProgressListener? = null
    ): MultipartBody.Part? {
        val fileName = customFileName ?: getFileName(context, uri)
        val mimeType = getMimeType(context, uri)
        val tempFile = copyUriToTempFile(context, uri, fileName) ?: return null

        val rawBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val body = if (onProgress != null) ProgressRequestBody(rawBody, onProgress) else rawBody

        return MultipartBody.Part.createFormData(partName, fileName, body)
    }

    /**
     * Constructs a [MultipartBody.Part] from a in-memory [ByteArray].
     *
     * @param bytes The binary byte array (e.g., compressed bitmap bytes).
     * @param partName The form-data key name.
     * @param fileName File name sent in the content-disposition header.
     * @param mimeType MIME type string (e.g. "image/jpeg", "image/png").
     * @param onProgress Optional callback receiving upload progress.
     */
    fun createPartFromBytes(
        bytes: ByteArray,
        partName: String = "file",
        fileName: String = "upload.jpg",
        mimeType: String = "image/jpeg",
        onProgress: UploadProgressListener? = null
    ): MultipartBody.Part {
        val rawBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val body = if (onProgress != null) ProgressRequestBody(rawBody, onProgress) else rawBody

        return MultipartBody.Part.createFormData(partName, fileName, body)
    }

    /**
     * Creates a standard `text/plain` [RequestBody] for simple text form fields.
     */
    fun createTextRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    /**
     * Converts a map of string parameters to a map of [RequestBody] instances
     * suitable for Retrofit `@PartMap Map<String, RequestBody>`.
     */
    fun createPartMap(params: Map<String, String>): Map<String, RequestBody> {
        return params.mapValues { createTextRequestBody(it.value) }
    }

    /**
     * Extracts the display file name from an Android content [Uri].
     */
    fun getFileName(context: Context, uri: Uri): String {
        var name = "upload_${System.currentTimeMillis()}"
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex) ?: name
                    }
                }
            }
        } else {
            uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) {
                    name = path.substring(cut + 1)
                }
            }
        }
        return name
    }

    /**
     * Detects the MIME type of a content [Uri].
     */
    fun getMimeType(context: Context, uri: Uri): String {
        if (uri.scheme == "content") {
            runCatching { context.contentResolver.getType(uri) }.getOrNull()?.let { return it }
        }
        val extension = uri.path?.substringAfterLast('.', "")?.lowercase() ?: ""
        return resolveMimeFromExtension(extension)
    }

    /**
     * Detects the MIME type of a local [File].
     */
    fun getMimeTypeFromFile(file: File): String {
        return resolveMimeFromExtension(file.extension.lowercase())
    }

    private fun resolveMimeFromExtension(extension: String): String {
        if (extension.isBlank()) return DEFAULT_MIME_TYPE

        // Direct mapping for common formats without needing Android framework mocks
        val knownMime = when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            "json" -> "application/json"
            "mp4" -> "video/mp4"
            "zip" -> "application/zip"
            "csv" -> "text/csv"
            else -> null
        }
        if (knownMime != null) return knownMime

        return runCatching {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }.getOrNull() ?: DEFAULT_MIME_TYPE
    }

    private fun copyUriToTempFile(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val extension = if (fileName.contains('.')) fileName.substringAfterLast('.') else "tmp"
            val prefix = if (fileName.contains('.')) fileName.substringBeforeLast('.') else "upload_"
            val safePrefix = prefix.take(15).padStart(3, '_')

            val tempFile = File.createTempFile(safePrefix, ".$extension", context.cacheDir)
            tempFile.deleteOnExit()

            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            tempFile
        } catch (_: Exception) {
            null
        }
    }
}
