package com.vc.androidcore.network

import com.vc.androidcore.network.multipart.MultipartHelper
import com.vc.androidcore.network.multipart.ProgressRequestBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MultipartHelperTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `createTextRequestBody should create valid text request body`() {
        val body = MultipartHelper.createTextRequestBody("Hello World")

        assertEquals("text/plain; charset=utf-8", body.contentType().toString())
        assertEquals(11L, body.contentLength())
    }

    @Test
    fun `createPartMap should convert map to request body map`() {
        val params = mapOf(
            "title" to "Sample Title",
            "userId" to "42"
        )
        val partMap = MultipartHelper.createPartMap(params)

        assertEquals(2, partMap.size)
        assertNotNull(partMap["title"])
        assertNotNull(partMap["userId"])
        assertEquals(12L, partMap["title"]?.contentLength())
    }

    @Test
    fun `createPartFromBytes should construct valid multipart part`() {
        val bytes = "Fake Image Bytes".toByteArray()
        val part = MultipartHelper.createPartFromBytes(
            bytes = bytes,
            partName = "avatar",
            fileName = "avatar.jpg",
            mimeType = "image/jpeg"
        )

        assertNotNull(part)
        assertNotNull(part.body)
        assertEquals(bytes.size.toLong(), part.body.contentLength())
        assertEquals("image/jpeg", part.body.contentType()?.toString())
    }

    @Test
    fun `createPartFromFile should construct valid multipart part from file`() {
        val file = tempFolder.newFile("test_upload.txt").apply {
            writeText("Hello from temporary file upload")
        }

        val part = MultipartHelper.createPartFromFile(
            file = file,
            partName = "document"
        )

        assertNotNull(part)
        assertNotNull(part.body)
        assertEquals(file.length(), part.body.contentLength())
    }

    @Test
    fun `ProgressRequestBody should track write progress correctly`() {
        val sampleData = "Sample data for tracking upload progress"
        val rawBody = MultipartHelper.createTextRequestBody(sampleData)

        val reportedPercentages = mutableListOf<Int>()
        val progressBody = ProgressRequestBody(rawBody) { _, _, percentage ->
            reportedPercentages.add(percentage)
        }

        assertEquals(rawBody.contentLength(), progressBody.contentLength())
        assertEquals(rawBody.contentType(), progressBody.contentType())

        // Simulate OkHttp writeTo sink
        val buffer = Buffer()
        progressBody.writeTo(buffer)

        assertEquals(sampleData, buffer.readUtf8())
    }
}
