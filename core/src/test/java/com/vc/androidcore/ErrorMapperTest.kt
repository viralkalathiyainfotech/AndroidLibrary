package com.vc.androidcore

import com.vc.androidcore.error.AppError
import com.vc.androidcore.error.ErrorMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMapperTest {

    @Test
    fun map_timeoutException_returnsTimeoutError() {
        val result = ErrorMapper.map(SocketTimeoutException("Read timed out"))
        assertEquals(AppError.Timeout, result)
    }

    @Test
    fun map_unknownHostException_returnsNetworkError() {
        val result = ErrorMapper.map(UnknownHostException("Unable to resolve host"))
        assertEquals(AppError.Network, result)
    }

    @Test
    fun map_connectException_returnsNetworkError() {
        val result = ErrorMapper.map(ConnectException("Connection refused"))
        assertEquals(AppError.Network, result)
    }

    @Test
    fun mapHttpCode_401_returnsUnauthorized() {
        val result = ErrorMapper.mapHttpCode(401)
        assertEquals(AppError.Unauthorized, result)
    }

    @Test
    fun mapHttpCode_403_returnsForbidden() {
        val result = ErrorMapper.mapHttpCode(403)
        assertEquals(AppError.Forbidden, result)
    }

    @Test
    fun mapHttpCode_404_returnsNotFound() {
        val result = ErrorMapper.mapHttpCode(404)
        assertEquals(AppError.NotFound, result)
    }

    @Test
    fun mapHttpCode_422_returnsValidation() {
        val result = ErrorMapper.mapHttpCode(422, "Email is invalid")
        assertTrue(result is AppError.Validation)
        assertEquals("Email is invalid", (result as AppError.Validation).message)
    }

    @Test
    fun mapHttpCode_500_returnsServer() {
        val result = ErrorMapper.mapHttpCode(500, "Internal error")
        assertTrue(result is AppError.Server)
        assertEquals(500, (result as AppError.Server).code)
    }

    @Test
    fun map_genericException_returnsUnknown() {
        val result = ErrorMapper.map(IllegalStateException("Something went wrong"))
        assertTrue(result is AppError.Unknown)
    }
}
