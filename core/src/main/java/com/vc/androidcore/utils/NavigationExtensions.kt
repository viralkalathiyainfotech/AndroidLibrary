package com.vc.androidcore.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import java.io.Serializable

// ============================================================================
// 1. CONTEXT / ACTIVITY NAVIGATION EXTENSIONS
// ============================================================================

/**
 * Starts an [Activity] of type [T].
 *
 * Example:
 * ```kotlin
 * // Zero boilerplate
 * startActivity<LoginActivity>()
 *
 * // With finish current screen
 * startActivity<LoginActivity>(finishCurrent = true)
 *
 * // With custom intent configuration lambda
 * startActivity<LoginActivity> {
 *     putExtra("is_relogin", true)
 * }
 * ```
 */
inline fun <reified T : Activity> Context.startActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java).apply {
        init?.invoke(this)
    }
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent, options)
    if (finishCurrent && this is Activity) {
        finish()
    }
}

/**
 * Starts an [Activity] of type [T] with key-value pairs as extras.
 *
 * Example:
 * ```kotlin
 * startActivity<UserDetailActivity>(
 *     "user_id" to 101,
 *     "user_name" to "Viral",
 *     "is_active" to true
 * )
 * ```
 */
inline fun <reified T : Activity> Context.startActivity(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    finishCurrent: Boolean = false,
    options: Bundle? = null
) {
    val intent = Intent(this, T::class.java).apply {
        putExtra(first)
        putExtras(*rest)
    }
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent, options)
    if (finishCurrent && this is Activity) {
        finish()
    }
}

/**
 * Starts an [Activity] of type [T] and immediately finishes the calling [Activity].
 *
 * Example:
 * ```kotlin
 * startActivityAndFinish<HomeActivity>()
 * ```
 */
inline fun <reified T : Activity> Activity.startActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    startActivity<T>(finishCurrent = true, options = options, init = init)
}

/**
 * Starts an [Activity] of type [T] with key-value extras and finishes the calling [Activity].
 */
inline fun <reified T : Activity> Activity.startActivityAndFinish(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    options: Bundle? = null
) {
    startActivity<T>(first, *rest, finishCurrent = true, options = options)
}

/**
 * Clears the entire activity back-stack and opens [T] as the new root screen.
 * Ideal for Logout, Splash to Login, or Login to Home flows.
 *
 * Example:
 * ```kotlin
 * startActivityClearTask<LoginActivity>()
 * ```
 */
inline fun <reified T : Activity> Context.startActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        init?.invoke(this)
    }
    startActivity(intent, options)
    if (this is Activity) {
        finish()
    }
}

/**
 * Clears the entire back-stack and opens [T] with key-value extras.
 */
inline fun <reified T : Activity> Context.startActivityClearTask(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    options: Bundle? = null
) {
    val intent = Intent(this, T::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra(first)
        putExtras(*rest)
    }
    startActivity(intent, options)
    if (this is Activity) {
        finish()
    }
}

/**
 * Starts [T] with `FLAG_ACTIVITY_CLEAR_TOP` and `FLAG_ACTIVITY_SINGLE_TOP`.
 */
inline fun <reified T : Activity> Context.startActivityClearTop(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        init?.invoke(this)
    }
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent, options)
}

/**
 * Starts [T] with `FLAG_ACTIVITY_SINGLE_TOP`.
 */
inline fun <reified T : Activity> Context.startActivitySingleTop(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        init?.invoke(this)
    }
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent, options)
}

/**
 * Safely attempts to start an activity with the provided [Intent].
 * Returns `true` if successful, or `false` if no activity was found to handle it.
 */
fun Context.startActivitySafely(intent: Intent, options: Bundle? = null): Boolean {
    return try {
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent, options)
        true
    } catch (e: Exception) {
        false
    }
}

// ============================================================================
// 2. ALIASES: openActivity & launchActivity
// ============================================================================

inline fun <reified T : Activity> Context.openActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivity<T>(finishCurrent = finishCurrent, options = options, init = init)

inline fun <reified T : Activity> Context.openActivity(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    finishCurrent: Boolean = false,
    options: Bundle? = null
) = startActivity<T>(first, *rest, finishCurrent = finishCurrent, options = options)

inline fun <reified T : Activity> Activity.openActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityAndFinish<T>(options = options, init = init)

inline fun <reified T : Activity> Activity.openActivityAndFinish(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    options: Bundle? = null
) = startActivityAndFinish<T>(first, *rest, options = options)

inline fun <reified T : Activity> Context.openActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityClearTask<T>(options = options, init = init)

inline fun <reified T : Activity> Context.launchActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivity<T>(finishCurrent = finishCurrent, options = options, init = init)

inline fun <reified T : Activity> Context.launchActivity(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    finishCurrent: Boolean = false,
    options: Bundle? = null
) = startActivity<T>(first, *rest, finishCurrent = finishCurrent, options = options)

inline fun <reified T : Activity> Activity.launchActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityAndFinish<T>(options = options, init = init)

inline fun <reified T : Activity> Context.launchActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityClearTask<T>(options = options, init = init)

// ============================================================================
// 3. ACTIVITY RESULT & LIFECYCLE SHORTCUTS
// ============================================================================

/**
 * Finishes the [Activity] with [Activity.RESULT_OK] and key-value extras.
 *
 * Example:
 * ```kotlin
 * finishWithResultOk("is_updated" to true, "item_id" to 42)
 * ```
 */
fun Activity.finishWithResultOk(first: Pair<String, Any?>, vararg rest: Pair<String, Any?>) {
    val resultIntent = Intent().apply {
        putExtra(first)
        putExtras(*rest)
    }
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

/**
 * Finishes the [Activity] with [Activity.RESULT_OK] and optional intent configuration block.
 *
 * Example:
 * ```kotlin
 * finishWithResultOk {
 *     putExtra("is_updated", true)
 * }
 * ```
 */
fun Activity.finishWithResultOk(init: (Intent.() -> Unit)? = null) {
    val resultIntent = if (init != null) Intent().apply(init) else Intent()
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

/**
 * Finishes the [Activity] with [Activity.RESULT_CANCELED].
 *
 * Example:
 * ```kotlin
 * finishWithResultCanceled()
 * ```
 */
fun Activity.finishWithResultCanceled() {
    setResult(Activity.RESULT_CANCELED)
    finish()
}

/**
 * Restarts the current [Activity] cleanly.
 * Useful for immediate application of theme, font size, or language changes.
 *
 * Example:
 * ```kotlin
 * restartActivity()
 * ```
 */
fun Activity.restartActivity() {
    finish()
    startActivity(intent)
}

// ============================================================================
// 4. FRAGMENT NAVIGATION EXTENSIONS
// ============================================================================

inline fun <reified T : Activity> Fragment.startActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    requireContext().startActivity<T>(
        finishCurrent = finishCurrent && activity != null,
        options = options,
        init = init
    )
    if (finishCurrent) {
        activity?.finish()
    }
}

inline fun <reified T : Activity> Fragment.startActivity(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    finishCurrent: Boolean = false,
    options: Bundle? = null
) {
    requireContext().startActivity<T>(
        first,
        *rest,
        finishCurrent = finishCurrent && activity != null,
        options = options
    )
    if (finishCurrent) {
        activity?.finish()
    }
}

inline fun <reified T : Activity> Fragment.startActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    startActivity<T>(finishCurrent = true, options = options, init = init)
}

inline fun <reified T : Activity> Fragment.startActivityAndFinish(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    options: Bundle? = null
) {
    startActivity<T>(first, *rest, finishCurrent = true, options = options)
}

inline fun <reified T : Activity> Fragment.startActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) {
    requireContext().startActivityClearTask<T>(options = options, init = init)
    activity?.finish()
}

inline fun <reified T : Activity> Fragment.openActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivity<T>(finishCurrent = finishCurrent, options = options, init = init)

inline fun <reified T : Activity> Fragment.openActivity(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>,
    finishCurrent: Boolean = false,
    options: Bundle? = null
) = startActivity<T>(first, *rest, finishCurrent = finishCurrent, options = options)

inline fun <reified T : Activity> Fragment.openActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityAndFinish<T>(options = options, init = init)

inline fun <reified T : Activity> Fragment.openActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityClearTask<T>(options = options, init = init)

inline fun <reified T : Activity> Fragment.launchActivity(
    finishCurrent: Boolean = false,
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivity<T>(finishCurrent = finishCurrent, options = options, init = init)

inline fun <reified T : Activity> Fragment.launchActivityAndFinish(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityAndFinish<T>(options = options, init = init)

inline fun <reified T : Activity> Fragment.launchActivityClearTask(
    options: Bundle? = null,
    noinline init: (Intent.() -> Unit)? = null
) = startActivityClearTask<T>(options = options, init = init)

fun Fragment.startActivitySafely(intent: Intent, options: Bundle? = null): Boolean =
    requireContext().startActivitySafely(intent, options)

// ============================================================================
// 5. INTENT CREATION HELPERS
// ============================================================================

/**
 * Creates an [Intent] for [Activity] of type [T] with optional config lambda.
 *
 * Example:
 * ```kotlin
 * val intent = intentOf<LoginActivity>()
 * val intent = intentOf<LoginActivity> { putExtra("source", "splash") }
 * ```
 */
inline fun <reified T : Activity> Context.intentOf(
    noinline init: (Intent.() -> Unit)? = null
): Intent = Intent(this, T::class.java).apply {
    init?.invoke(this)
}

/**
 * Creates an [Intent] for [Activity] of type [T] with key-value extras.
 *
 * Example:
 * ```kotlin
 * val intent = intentOf<UserDetailActivity>("id" to 42, "role" to "admin")
 * ```
 */
inline fun <reified T : Activity> Context.intentOf(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>
): Intent = Intent(this, T::class.java).apply {
    putExtra(first)
    putExtras(*rest)
}

inline fun <reified T : Activity> Context.createIntent(
    noinline init: (Intent.() -> Unit)? = null
): Intent = intentOf<T>(init)

inline fun <reified T : Activity> Context.createIntent(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>
): Intent = intentOf<T>(first, *rest)

inline fun <reified T : Activity> Fragment.intentOf(
    noinline init: (Intent.() -> Unit)? = null
): Intent = requireContext().intentOf<T>(init)

inline fun <reified T : Activity> Fragment.intentOf(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>
): Intent = requireContext().intentOf<T>(first, *rest)

inline fun <reified T : Activity> Fragment.createIntent(
    noinline init: (Intent.() -> Unit)? = null
): Intent = intentOf<T>(init)

inline fun <reified T : Activity> Fragment.createIntent(
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>
): Intent = intentOf<T>(first, *rest)

// ============================================================================
// 6. SAFE PENDING INTENT HELPERS (Android 12+ API 31 Flag Protected)
// ============================================================================

/**
 * Creates a safe [PendingIntent] for [Activity] of type [T].
 * Automatically includes [PendingIntent.FLAG_IMMUTABLE] on Android 12+ (API 31+).
 *
 * Example:
 * ```kotlin
 * val pendingIntent = context.pendingIntentOf<NotificationActivity>(
 *     requestCode = 100,
 *     first = "notification_id" to 42
 * )
 * ```
 */
inline fun <reified T : Activity> Context.pendingIntentOf(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    isMutable: Boolean = false,
    noinline init: (Intent.() -> Unit)? = null
): PendingIntent {
    val intent = intentOf<T>(init)
    val finalFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val mutabilityFlag = if (isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else 0
        flags or mutabilityFlag
    } else {
        flags
    }
    return PendingIntent.getActivity(this, requestCode, intent, finalFlags)
}

inline fun <reified T : Activity> Context.pendingIntentOf(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    isMutable: Boolean = false,
    first: Pair<String, Any?>,
    vararg rest: Pair<String, Any?>
): PendingIntent {
    val intent = intentOf<T>(first, *rest)
    val finalFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val mutabilityFlag = if (isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else 0
        flags or mutabilityFlag
    } else {
        flags
    }
    return PendingIntent.getActivity(this, requestCode, intent, finalFlags)
}

// ============================================================================
// 7. INTENT & BUNDLE EXTRAS PUTTERS & GETTERS
// ============================================================================

/**
 * Puts multiple key-value pairs into the [Intent] extras.
 */
fun Intent.putExtras(vararg pairs: Pair<String, Any?>): Intent {
    pairs.forEach { putExtra(it) }
    return this
}

/**
 * Puts a single key-value pair into the [Intent] extras with automatic type inference.
 */
fun Intent.putExtra(pair: Pair<String, Any?>): Intent {
    val (key, value) = pair
    when (value) {
        null -> putExtra(key, null as Serializable?)
        is Int -> putExtra(key, value)
        is Long -> putExtra(key, value)
        is String -> putExtra(key, value)
        is Boolean -> putExtra(key, value)
        is Float -> putExtra(key, value)
        is Double -> putExtra(key, value)
        is Char -> putExtra(key, value)
        is Short -> putExtra(key, value)
        is Byte -> putExtra(key, value)
        is CharSequence -> putExtra(key, value)
        is Bundle -> putExtra(key, value)
        is Parcelable -> putExtra(key, value)
        is Serializable -> putExtra(key, value)
        is IntArray -> putExtra(key, value)
        is LongArray -> putExtra(key, value)
        is FloatArray -> putExtra(key, value)
        is DoubleArray -> putExtra(key, value)
        is BooleanArray -> putExtra(key, value)
        is ByteArray -> putExtra(key, value)
        is CharArray -> putExtra(key, value)
        is ShortArray -> putExtra(key, value)
        is Array<*> -> {
            when {
                value.isArrayOf<String>() -> @Suppress("UNCHECKED_CAST") putExtra(key, value as Array<String>)
                value.isArrayOf<CharSequence>() -> @Suppress("UNCHECKED_CAST") putExtra(key, value as Array<CharSequence>)
                value.isArrayOf<Parcelable>() -> @Suppress("UNCHECKED_CAST") putExtra(key, value as Array<Parcelable>)
                else -> throw IllegalArgumentException("Unsupported array type for extra '$key'")
            }
        }
        is ArrayList<*> -> {
            when {
                (value.isNotEmpty() && value.first() is String) -> @Suppress("UNCHECKED_CAST") putStringArrayListExtra(key, value as ArrayList<String>)
                (value.isNotEmpty() && value.first() is Int) -> @Suppress("UNCHECKED_CAST") putIntegerArrayListExtra(key, value as ArrayList<Int>)
                (value.isNotEmpty() && value.first() is CharSequence) -> @Suppress("UNCHECKED_CAST") putCharSequenceArrayListExtra(key, value as ArrayList<CharSequence>)
                (value.isNotEmpty() && value.first() is Parcelable) -> @Suppress("UNCHECKED_CAST") putParcelableArrayListExtra(key, value as ArrayList<Parcelable>)
                value.isEmpty() -> putStringArrayListExtra(key, ArrayList())
                else -> putExtra(key, value as Serializable)
            }
        }
        else -> throw IllegalArgumentException("Intent extra '$key' of type ${value::class.java.name} is not supported")
    }
    return this
}

// Extra reading shortcuts
fun Intent.extra(key: String, default: String): String = getStringExtra(key) ?: default
fun Intent.extra(key: String, default: Int): Int = getIntExtra(key, default)
fun Intent.extra(key: String, default: Boolean): Boolean = getBooleanExtra(key, default)
fun Intent.extra(key: String, default: Long): Long = getLongExtra(key, default)
fun Intent.extra(key: String, default: Double): Double = getDoubleExtra(key, default)
fun Intent.extra(key: String, default: Float): Float = getFloatExtra(key, default)

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <T> Intent.extra(key: String, default: T? = null): T? {
    val value = extras?.get(key)
    return (value as? T) ?: default
}

/**
 * Android 13+ (API 33) type-safe [Parcelable] extra reader.
 */
inline fun <reified T : Parcelable> Intent.parcelableExtra(key: String): T? {
    return IntentCompat.getParcelableExtra(this, key, T::class.java)
}

/**
 * Android 13+ (API 33) type-safe [Serializable] extra reader.
 */
inline fun <reified T : Serializable> Intent.serializableExtra(key: String): T? {
    return IntentCompat.getSerializableExtra(this, key, T::class.java)
}

/**
 * Puts multiple key-value pairs into a [Bundle].
 */
fun Bundle.putExtras(vararg pairs: Pair<String, Any?>): Bundle {
    pairs.forEach { (key, value) ->
        when (value) {
            null -> putSerializable(key, null as Serializable?)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Double -> putDouble(key, value)
            is Char -> putChar(key, value)
            is Short -> putShort(key, value)
            is Byte -> putByte(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Bundle -> putBundle(key, value)
            is Parcelable -> putParcelable(key, value)
            is Serializable -> putSerializable(key, value)
            is IntArray -> putIntArray(key, value)
            is LongArray -> putLongArray(key, value)
            is FloatArray -> putFloatArray(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is BooleanArray -> putBooleanArray(key, value)
            is ByteArray -> putByteArray(key, value)
            is CharArray -> putCharArray(key, value)
            is ShortArray -> putShortArray(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<String>() -> @Suppress("UNCHECKED_CAST") putStringArray(key, value as Array<String>)
                    value.isArrayOf<CharSequence>() -> @Suppress("UNCHECKED_CAST") putCharSequenceArray(key, value as Array<CharSequence>)
                    value.isArrayOf<Parcelable>() -> @Suppress("UNCHECKED_CAST") putParcelableArray(key, value as Array<Parcelable>)
                    else -> throw IllegalArgumentException("Unsupported array type for Bundle extra '$key'")
                }
            }
            is ArrayList<*> -> {
                when {
                    (value.isNotEmpty() && value.first() is String) -> @Suppress("UNCHECKED_CAST") putStringArrayList(key, value as ArrayList<String>)
                    (value.isNotEmpty() && value.first() is Int) -> @Suppress("UNCHECKED_CAST") putIntegerArrayList(key, value as ArrayList<Int>)
                    (value.isNotEmpty() && value.first() is CharSequence) -> @Suppress("UNCHECKED_CAST") putCharSequenceArrayList(key, value as ArrayList<CharSequence>)
                    (value.isNotEmpty() && value.first() is Parcelable) -> @Suppress("UNCHECKED_CAST") putParcelableArrayList(key, value as ArrayList<Parcelable>)
                    value.isEmpty() -> putStringArrayList(key, ArrayList())
                    else -> putSerializable(key, value as Serializable)
                }
            }
            else -> throw IllegalArgumentException("Bundle extra '$key' of type ${value::class.java.name} is not supported")
        }
    }
    return this
}

/**
 * Creates a [Bundle] containing key-value pairs.
 *
 * Example:
 * ```kotlin
 * val bundle = bundleOfExtras("id" to 101, "name" to "Viral")
 * ```
 */
fun bundleOfExtras(vararg pairs: Pair<String, Any?>): Bundle {
    return Bundle().apply { putExtras(*pairs) }
}

/**
 * Android 13+ (API 33) type-safe [Parcelable] bundle reader.
 */
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? {
    return BundleCompat.getParcelable(this, key, T::class.java)
}

/**
 * Android 13+ (API 33) type-safe [Serializable] bundle reader.
 */
inline fun <reified T : Serializable> Bundle.serializable(key: String): T? {
    return BundleCompat.getSerializable(this, key, T::class.java)
}

// ============================================================================
// 8. PROPERTY DELEGATES FOR EXTRAS & FRAGMENT ARGUMENTS
// ============================================================================

/**
 * Lazily retrieves a non-null Intent extra with a default fallback.
 *
 * Example:
 * ```kotlin
 * private val userId: Int by extra("user_id", -1)
 * private val userName: String by extra("user_name", "Guest")
 * ```
 */
fun <T> Activity.extra(key: String, default: T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    (intent.extras?.get(key) as? T) ?: default
}

/**
 * Lazily retrieves a nullable Intent extra.
 *
 * Example:
 * ```kotlin
 * private val token: String? by extraOrNull("auth_token")
 * ```
 */
fun <T> Activity.extraOrNull(key: String): Lazy<T?> = lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    intent.extras?.get(key) as? T
}

/**
 * Lazily retrieves a [Parcelable] Intent extra (Android 13+ safe).
 *
 * Example:
 * ```kotlin
 * private val user: User? by extraParcelable("user_data")
 * ```
 */
inline fun <reified T : Parcelable> Activity.extraParcelable(key: String): Lazy<T?> =
    lazy(LazyThreadSafetyMode.NONE) {
        intent.parcelableExtra<T>(key)
    }

/**
 * Lazily retrieves a [Serializable] Intent extra (Android 13+ safe).
 */
inline fun <reified T : Serializable> Activity.extraSerializable(key: String): Lazy<T?> =
    lazy(LazyThreadSafetyMode.NONE) {
        intent.serializableExtra<T>(key)
    }

/**
 * Attaches key-value arguments to a [Fragment] before it is attached.
 *
 * Example:
 * ```kotlin
 * val fragment = UserDetailFragment().withArgs(
 *     "user_id" to 42,
 *     "is_admin" to true
 * )
 * ```
 */
fun <T : Fragment> T.withArgs(vararg pairs: Pair<String, Any?>): T {
    val bundle = arguments ?: Bundle()
    bundle.putExtras(*pairs)
    arguments = bundle
    return this
}

/**
 * Creates a new instance of [Fragment] of type [T] with key-value arguments.
 *
 * Example:
 * ```kotlin
 * val fragment = newFragment<UserDetailFragment>("user_id" to 42)
 * ```
 */
inline fun <reified T : Fragment> newFragment(vararg pairs: Pair<String, Any?>): T {
    val fragment = T::class.java.getDeclaredConstructor().newInstance()
    if (pairs.isNotEmpty()) {
        fragment.withArgs(*pairs)
    }
    return fragment
}

/**
 * Lazily retrieves a non-null Fragment argument with a default fallback.
 *
 * Example:
 * ```kotlin
 * private val userId: Int by arg("user_id", -1)
 * ```
 */
fun <T> Fragment.arg(key: String, default: T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    (arguments?.get(key) as? T) ?: default
}

/**
 * Lazily retrieves a nullable Fragment argument.
 *
 * Example:
 * ```kotlin
 * private val token: String? by argOrNull("auth_token")
 * ```
 */
fun <T> Fragment.argOrNull(key: String): Lazy<T?> = lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    arguments?.get(key) as? T
}

/**
 * Lazily retrieves a [Parcelable] Fragment argument (Android 13+ safe).
 */
inline fun <reified T : Parcelable> Fragment.argParcelable(key: String): Lazy<T?> =
    lazy(LazyThreadSafetyMode.NONE) {
        arguments?.parcelable<T>(key)
    }

/**
 * Lazily retrieves a [Serializable] Fragment argument (Android 13+ safe).
 */
inline fun <reified T : Serializable> Fragment.argSerializable(key: String): Lazy<T?> =
    lazy(LazyThreadSafetyMode.NONE) {
        arguments?.serializable<T>(key)
    }

// ============================================================================
// 9. SYSTEM INTENTS SHORTCUTS (URL, Share, Phone, Email, Settings, Play Store)
// ============================================================================

/**
 * Opens a web URL in the default browser.
 */
fun Context.openUrl(url: String): Boolean {
    return try {
        val parsedUri = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Uri.parse("https://$url")
        } else {
            Uri.parse(url)
        }
        val intent = Intent(Intent.ACTION_VIEW, parsedUri).apply {
            if (this@openUrl !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Opens the system share sheet with the specified text.
 */
fun Context.shareText(text: String, title: String? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, title).apply {
        if (this@shareText !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    startActivity(chooser)
}

/**
 * Opens the phone dialer with the given phone number pre-filled.
 */
fun Context.dialNumber(phoneNumber: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.trim()}")).apply {
            if (this@dialNumber !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Opens email client to send an email.
 */
fun Context.sendEmail(to: String, subject: String = "", body: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            if (subject.isNotEmpty()) putExtra(Intent.EXTRA_SUBJECT, subject)
            if (body.isNotEmpty()) putExtra(Intent.EXTRA_TEXT, body)
            if (this@sendEmail !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Opens this app's details screen in system Settings (useful for permission rationale).
 */
fun Context.openAppSettings(): Boolean {
    return try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            if (this@openAppSettings !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Opens an app on Google Play Store. Defaults to the current app if no package name is given.
 */
fun Context.openPlayStore(targetPackage: String = packageName): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$targetPackage")).apply {
            if (this@openPlayStore !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        openUrl("https://play.google.com/store/apps/details?id=$targetPackage")
    }
}

/**
 * Opens a location on Google Maps or other maps app using coordinates.
 */
fun Context.openMapLocation(latitude: Double, longitude: Double, label: String? = null): Boolean {
    val uri = if (label != null) {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
    } else {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        if (this@openMapLocation !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    return startActivitySafely(intent)
}

/**
 * Opens WhatsApp chat directly with a given phone number and optional message.
 */
fun Context.openWhatsAppChat(phoneNumberWithCountryCode: String, message: String = ""): Boolean {
    val cleanNumber = phoneNumberWithCountryCode.replace("+", "").replace(" ", "").trim()
    val encodedMessage = Uri.encode(message)
    val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"
    return openUrl(url)
}

fun Fragment.openUrl(url: String): Boolean = requireContext().openUrl(url)
fun Fragment.shareText(text: String, title: String? = null) = requireContext().shareText(text, title)
fun Fragment.dialNumber(phoneNumber: String): Boolean = requireContext().dialNumber(phoneNumber)
fun Fragment.sendEmail(to: String, subject: String = "", body: String = ""): Boolean =
    requireContext().sendEmail(to, subject, body)
fun Fragment.openAppSettings(): Boolean = requireContext().openAppSettings()
fun Fragment.openPlayStore(targetPackage: String = requireContext().packageName): Boolean =
    requireContext().openPlayStore(targetPackage)
fun Fragment.openMapLocation(latitude: Double, longitude: Double, label: String? = null): Boolean =
    requireContext().openMapLocation(latitude, longitude, label)
fun Fragment.openWhatsAppChat(phoneNumberWithCountryCode: String, message: String = ""): Boolean =
    requireContext().openWhatsAppChat(phoneNumberWithCountryCode, message)
