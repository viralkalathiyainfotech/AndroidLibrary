package com.vc.androidcore

import com.vc.androidcore.error.AppError
import com.vc.androidcore.network.NetworkResult
import com.vc.androidcore.network.safeApiCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun safeApiCall_onSuccessfulResponse_returnsSuccess() = runTest(testDispatcher) {
        val result = safeApiCall(testDispatcher) {
            Response.success("Success Data")
        }

        assertTrue(result is NetworkResult.Success)
        assertEquals("Success Data", (result as NetworkResult.Success).data)
    }

    @Test
    fun safeApiCall_onHttpError_returnsErrorWithCode() = runTest(testDispatcher) {
        val result = safeApiCall(testDispatcher) {
            Response.error<String>(401, "Unauthorized access".toResponseBody(null))
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(401, error.code)
        assertEquals(AppError.Unauthorized, error.appError)
    }

    @Test
    fun safeApiCall_onException_returnsErrorWithMappedAppError() = runTest(testDispatcher) {
        val result = safeApiCall<String>(testDispatcher) {
            throw SocketTimeoutException("Connection timed out")
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(AppError.Timeout, error.appError)
    }

    @Test
    fun safeApiCall_retriesOnTransientError_andSucceeds() = runTest(testDispatcher) {
        var attempts = 0
        val result = safeApiCall(testDispatcher, retryCount = 2, retryDelayMs = 10) {
            attempts++
            if (attempts < 2) {
                Response.error<String>(503, "Service Unavailable".toResponseBody(null))
            } else {
                Response.success("Recovered Data")
            }
        }

        assertTrue(result is NetworkResult.Success)
        assertEquals("Recovered Data", (result as NetworkResult.Success).data)
        assertEquals(2, attempts)
    }

    @Test
    fun safeApiCall_retriesExhausted_returnsError() = runTest(testDispatcher) {
        var attempts = 0
        val result = safeApiCall(testDispatcher, retryCount = 2, retryDelayMs = 10) {
            attempts++
            Response.error<String>(500, "Internal Server Error".toResponseBody(null))
        }

        assertTrue(result is NetworkResult.Error)
        assertEquals(500, (result as NetworkResult.Error).code)
        assertEquals(3, attempts)
    }
}
