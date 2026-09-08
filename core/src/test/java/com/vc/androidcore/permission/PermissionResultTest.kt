package com.vc.androidcore.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionResultTest {

    @Test
    fun `all permissions granted should return true for areAllGranted`() {
        val result = PermissionResult(
            granted = listOf("android.permission.CAMERA", "android.permission.RECORD_AUDIO"),
            denied = emptyList(),
            permanentlyDenied = emptyList()
        )

        assertTrue(result.areAllGranted)
        assertFalse(result.hasDenied)
        assertFalse(result.hasPermanentlyDenied)
        assertTrue(result.isGranted("android.permission.CAMERA"))
        assertTrue(result.isGranted("android.permission.RECORD_AUDIO"))
    }

    @Test
    fun `denied permissions should reflect in hasDenied and areAllGranted`() {
        val result = PermissionResult(
            granted = listOf("android.permission.CAMERA"),
            denied = listOf("android.permission.ACCESS_FINE_LOCATION"),
            permanentlyDenied = emptyList()
        )

        assertFalse(result.areAllGranted)
        assertTrue(result.hasDenied)
        assertFalse(result.hasPermanentlyDenied)
        assertTrue(result.isGranted("android.permission.CAMERA"))
        assertFalse(result.isGranted("android.permission.ACCESS_FINE_LOCATION"))
    }

    @Test
    fun `permanently denied permissions should be recognized`() {
        val result = PermissionResult(
            granted = emptyList(),
            denied = emptyList(),
            permanentlyDenied = listOf("android.permission.POST_NOTIFICATIONS")
        )

        assertFalse(result.areAllGranted)
        assertFalse(result.hasDenied)
        assertTrue(result.hasPermanentlyDenied)
        assertTrue(result.isPermanentlyDenied("android.permission.POST_NOTIFICATIONS"))
    }

    @Test
    fun `empty granted permissions should not be considered all granted`() {
        val result = PermissionResult(
            granted = emptyList(),
            denied = emptyList(),
            permanentlyDenied = emptyList()
        )

        assertFalse(result.areAllGranted)
        assertFalse(result.hasDenied)
        assertFalse(result.hasPermanentlyDenied)
    }
}
