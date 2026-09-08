package com.vc.androidcore.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Modern, boilerplate-free runtime permission manager using Android's Activity Result API.
 * Supports both [ComponentActivity] and [Fragment] lifecycle contexts.
 */
class PermissionHelper {

    private val contextProvider: () -> Context
    private val rationaleChecker: (String) -> Boolean
    private val singleLauncher: ActivityResultLauncher<String>
    private val multiLauncher: ActivityResultLauncher<Array<String>>

    private var singleCallback: ((Boolean) -> Unit)? = null
    private var multiCallback: ((PermissionResult) -> Unit)? = null

    /**
     * Constructor for [ComponentActivity].
     */
    constructor(activity: ComponentActivity) {
        this.contextProvider = { activity }
        this.rationaleChecker = { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
        this.singleLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            singleCallback?.invoke(isGranted)
            singleCallback = null
        }
        this.multiLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultMap ->
            val granted = mutableListOf<String>()
            val denied = mutableListOf<String>()
            val permanentlyDenied = mutableListOf<String>()

            resultMap.forEach { (permission, isGranted) ->
                if (isGranted) {
                    granted.add(permission)
                } else {
                    if (!rationaleChecker(permission)) {
                        permanentlyDenied.add(permission)
                    } else {
                        denied.add(permission)
                    }
                }
            }
            multiCallback?.invoke(PermissionResult(granted, denied, permanentlyDenied))
            multiCallback = null
        }
    }

    /**
     * Constructor for [Fragment].
     */
    constructor(fragment: Fragment) {
        this.contextProvider = { fragment.requireContext() }
        this.rationaleChecker = { permission ->
            fragment.shouldShowRequestPermissionRationale(permission)
        }
        this.singleLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            singleCallback?.invoke(isGranted)
            singleCallback = null
        }
        this.multiLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultMap ->
            val granted = mutableListOf<String>()
            val denied = mutableListOf<String>()
            val permanentlyDenied = mutableListOf<String>()

            resultMap.forEach { (permission, isGranted) ->
                if (isGranted) {
                    granted.add(permission)
                } else {
                    if (!rationaleChecker(permission)) {
                        permanentlyDenied.add(permission)
                    } else {
                        denied.add(permission)
                    }
                }
            }
            multiCallback?.invoke(PermissionResult(granted, denied, permanentlyDenied))
            multiCallback = null
        }
    }

    /**
     * Checks whether a single [permission] is currently granted.
     */
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            contextProvider(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks whether all given [permissions] are currently granted.
     */
    fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.all { hasPermission(it) }
    }

    /**
     * Requests a single [permission].
     * If already granted, [onResult] is invoked immediately with `true`.
     */
    fun request(permission: String, onResult: (Boolean) -> Unit) {
        if (hasPermission(permission)) {
            onResult(true)
            return
        }
        singleCallback = onResult
        singleLauncher.launch(permission)
    }

    /**
     * Requests multiple [permissions] simultaneously.
     * If all are already granted, [onResult] is invoked immediately with all granted.
     */
    fun request(vararg permissions: String, onResult: (PermissionResult) -> Unit) {
        val notGranted = permissions.filter { !hasPermission(it) }
        if (notGranted.isEmpty()) {
            onResult(
                PermissionResult(
                    granted = permissions.toList(),
                    denied = emptyList(),
                    permanentlyDenied = emptyList()
                )
            )
            return
        }
        multiCallback = onResult
        multiLauncher.launch(notGranted.toTypedArray())
    }

    /**
     * Navigates the user directly to the application system settings screen.
     * Useful when a permission has been permanently denied.
     */
    fun openAppSettings() {
        val context = contextProvider()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
