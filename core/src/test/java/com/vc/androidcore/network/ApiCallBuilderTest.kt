package com.vc.androidcore.network

import com.vc.androidcore.error.AppError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ApiCallBuilderTest {

    @Test
    fun `default values should be properly initialized`() {
        val builder = ApiCallBuilder<String, String>()

        assertTrue(builder.showLoadingEnabled)
        assertEquals("Loading...", builder.loadingMessageText)
        assertTrue(builder.checkNetworkEnabled)
        assertEquals("No Internet connection available", builder.offlineMessageText)
        assertNull(builder.apiCallAction)
        assertNull(builder.transformAction)
        assertNull(builder.onSuccessAction)
        assertNull(builder.onErrorAction)
    }

    @Test
    fun `loading configuration should update loading properties`() {
        val builder = ApiCallBuilder<String, String>()
        builder.loading(show = false, message = "Please wait a moment...")

        assertFalse(builder.showLoadingEnabled)
        assertEquals("Please wait a moment...", builder.loadingMessageText)
    }

    @Test
    fun `checkNetwork configuration should update network properties`() {
        val builder = ApiCallBuilder<String, String>()
        builder.checkNetwork(check = false, offlineMessage = "Network unavailable")

        assertFalse(builder.checkNetworkEnabled)
        assertEquals("Network unavailable", builder.offlineMessageText)
    }

    @Test
    fun `retry configuration should update retry properties`() {
        val builder = ApiCallBuilder<String, String>()
        builder.retry(count = 3, delayMs = 2500L)

        assertEquals(3, builder.retryCountValue)
        assertEquals(2500L, builder.retryDelayMsValue)
    }

    @Test
    fun `request lambda should be captured and invokable`() = runBlocking {
        val builder = ApiCallBuilder<String, String>()
        builder.request {
            Response.success("api_response_body")
        }

        assertNotNull(builder.apiCallAction)
        val response = builder.apiCallAction!!.invoke()
        assertTrue(response.isSuccessful)
        assertEquals("api_response_body", response.body())
    }

    @Test
    fun `transform and onSuccess callbacks should execute correctly`() {
        data class RawDto(val id: Int, val name: String)
        data class DomainItem(val displayName: String)

        val builder = ApiCallBuilder<RawDto, DomainItem>()
        builder.transform { dto -> DomainItem("User #${dto.id}: ${dto.name}") }

        var resultReceived: DomainItem? = null
        builder.onSuccess { item -> resultReceived = item }

        val raw = RawDto(101, "Alice")
        val domain = builder.transformAction!!.invoke(raw)
        builder.onSuccessAction!!.invoke(domain)

        assertNotNull(resultReceived)
        assertEquals("User #101: Alice", resultReceived?.displayName)
    }

    @Test
    fun `onError callback should receive AppError`() {
        val builder = ApiCallBuilder<String, String>()
        var errorReceived: AppError? = null

        builder.onError { err -> errorReceived = err }

        val error = AppError.Server(500, "Internal Server Error")
        builder.onErrorAction?.invoke(error)

        assertEquals(error, errorReceived)
        assertEquals("Internal Server Error", (errorReceived as AppError.Server).message)
    }
}
