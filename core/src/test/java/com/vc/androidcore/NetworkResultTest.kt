package com.vc.androidcore

import com.vc.androidcore.network.NetworkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultTest {

    @Test
    fun successResult_returnsDataAndFlags() {
        val result = NetworkResult.Success("Hello")
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
        assertEquals("Hello", result.getOrNull())
    }

    @Test
    fun errorResult_returnsMessageAndErrorCode() {
        val result = NetworkResult.Error(code = 404, message = "Not Found")
        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertEquals(404, result.code)
        assertEquals("Not Found", result.message)
    }

    @Test
    fun map_transformsSuccessData() {
        val result = NetworkResult.Success(10)
        val mapped = result.map { it * 2 }
        assertTrue(mapped is NetworkResult.Success)
        assertEquals(20, (mapped as NetworkResult.Success).data)
    }
}
