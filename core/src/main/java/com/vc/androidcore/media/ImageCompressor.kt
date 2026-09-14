package com.vc.androidcore.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.roundToInt

/**
 * Configuration options for image compression.
 */
data class CompressionConfig(
    val maxFileSizeKb: Int = 500,
    val maxWidth: Int = 1920,
    val maxHeight: Int = 1080,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    val initialQuality: Int = 90,
    val minQuality: Int = 50
)

/**
 * High-performance, coroutine-powered Image Compressor utility that downscales large camera
 * resolutions, respects EXIF orientation, and iteratively compresses file size.
 *
 * Example:
 * ```kotlin
 * val compressedFile = ImageCompressor.compress(context, photoUri, maxFileSizeKb = 400)
 * ```
 */
object ImageCompressor {

    /**
     * Compresses an image given its [Uri] into a temporary [File] within app cache.
     */
    suspend fun compress(
        context: Context,
        imageUri: Uri,
        config: CompressionConfig = CompressionConfig()
    ): File = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: error("Unable to open input stream for URI: $imageUri")

        val orientation = getExifOrientation(context, imageUri)
        val bitmap = decodeSampledBitmap(inputStream, config.maxWidth, config.maxHeight, orientation)

        val outputDir = File(context.cacheDir, "compressed_images").apply { if (!exists()) mkdirs() }
        val extension = when (config.format) {
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.WEBP, Bitmap.CompressFormat.WEBP_LOSSY, Bitmap.CompressFormat.WEBP_LOSSLESS -> ".webp"
            else -> ".jpg"
        }
        val outputFile = File(outputDir, "img_${System.currentTimeMillis()}$extension")

        compressBitmapToFile(bitmap, outputFile, config)
        bitmap.recycle()
        outputFile
    }

    /**
     * Compresses an existing [File] into a smaller compressed [File].
     */
    suspend fun compress(
        context: Context,
        file: File,
        config: CompressionConfig = CompressionConfig()
    ): File = compress(context, Uri.fromFile(file), config)

    private fun decodeSampledBitmap(
        inputStream: InputStream,
        maxWidth: Int,
        maxHeight: Int,
        orientation: Int
    ): Bitmap {
        val bytes = inputStream.readBytes()
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false

        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: error("Failed to decode bitmap from bytes")

        return rotateBitmapIfRequired(rawBitmap, orientation)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getExifOrientation(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ExifInterface.ORIENTATION_NORMAL
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            inputStream.close()
            orientation
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    private fun rotateBitmapIfRequired(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) {
            bitmap.recycle()
        }
        return rotated
    }

    private fun compressBitmapToFile(bitmap: Bitmap, outputFile: File, config: CompressionConfig) {
        var quality = config.initialQuality
        var stream = ByteArrayOutputStream()

        bitmap.compress(config.format, quality, stream)
        val targetSizeBytes = config.maxFileSizeKb * 1024

        // Iteratively reduce quality if file is larger than target
        while (stream.size() > targetSizeBytes && quality > config.minQuality) {
            stream.reset()
            quality -= 10
            bitmap.compress(config.format, quality, stream)
        }

        FileOutputStream(outputFile).use { fos ->
            fos.write(stream.toByteArray())
            fos.flush()
        }
        stream.close()
    }
}

/**
 * Convenient extension to compress an image [Uri] to a cached [File].
 */
suspend fun Uri.compressImage(
    context: Context,
    maxFileSizeKb: Int = 500,
    maxWidth: Int = 1920,
    maxHeight: Int = 1080
): File {
    return ImageCompressor.compress(
        context = context,
        imageUri = this,
        config = CompressionConfig(
            maxFileSizeKb = maxFileSizeKb,
            maxWidth = maxWidth,
            maxHeight = maxHeight
        )
    )
}

/**
 * Convenient extension to directly convert an image [Uri] into a compressed [MultipartBody.Part]
 * ready for Retrofit API calls.
 *
 * Example:
 * ```kotlin
 * val avatarPart = imageUri.toCompressedMultipartPart(context, partName = "avatar")
 * apiService.uploadAvatar(avatarPart)
 * ```
 */
suspend fun Uri.toCompressedMultipartPart(
    context: Context,
    partName: String = "file",
    maxFileSizeKb: Int = 500
): MultipartBody.Part {
    val file = compressImage(context, maxFileSizeKb = maxFileSizeKb)
    val mimeType = context.contentResolver.getType(this) ?: "image/jpeg"
    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}
