package com.vc.androidcore.permission

/**
 * Detailed result of a runtime permission request.
 *
 * @property granted List of permissions granted by the user.
 * @property denied List of permissions denied by the user (can be asked again).
 * @property permanentlyDenied List of permissions permanently denied ("Don't ask again" checked).
 */
data class PermissionResult(
    val granted: List<String>,
    val denied: List<String> = emptyList(),
    val permanentlyDenied: List<String> = emptyList()
) {
    /**
     * True if all requested permissions were successfully granted.
     */
    val areAllGranted: Boolean get() = denied.isEmpty() && permanentlyDenied.isEmpty() && granted.isNotEmpty()

    /**
     * True if at least one permission was denied.
     */
    val hasDenied: Boolean get() = denied.isNotEmpty()

    /**
     * True if at least one permission was permanently denied ("Don't ask again").
     */
    val hasPermanentlyDenied: Boolean get() = permanentlyDenied.isNotEmpty()

    /**
     * Returns true if the specific [permission] is in the granted list.
     */
    fun isGranted(permission: String): Boolean = granted.contains(permission)

    /**
     * Returns true if the specific [permission] is in the permanently denied list.
     */
    fun isPermanentlyDenied(permission: String): Boolean = permanentlyDenied.contains(permission)
}
