package com.vc.composecore.resources

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * Clean UI resource abstraction enabling ViewModels to expose user-facing strings safely
 * without leaking Android [Context].
 */
@Immutable
sealed interface CoreText {

    data class Dynamic(val value: String) : CoreText

    data class Resource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : CoreText {
        constructor(@StringRes resId: Int, vararg formatArgs: Any) : this(resId, formatArgs.toList())
    }

    data class Plural(
        @param:PluralsRes val resId: Int,
        val quantity: Int,
        val args: List<Any> = emptyList()
    ) : CoreText {
        constructor(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any) : this(resId, quantity, formatArgs.toList())
    }

    data object Empty : CoreText

    /**
     * Resolves the text string within a Composable scope.
     */
    @Composable
    @ReadOnlyComposable
    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is Resource -> if (args.isEmpty()) stringResource(resId) else stringResource(resId, *args.toTypedArray())
            is Plural -> if (args.isEmpty()) pluralStringResource(resId, quantity) else pluralStringResource(resId, quantity, *args.toTypedArray())
            is Empty -> ""
        }
    }

    /**
     * Resolves the text string using a standard Android [Context].
     */
    fun asString(context: Context): String {
        return when (this) {
            is Dynamic -> value
            is Resource -> if (args.isEmpty()) context.getString(resId) else context.getString(resId, *args.toTypedArray())
            is Plural -> if (args.isEmpty()) context.resources.getQuantityString(resId, quantity) else context.resources.getQuantityString(resId, quantity, *args.toTypedArray())
            is Empty -> ""
        }
    }
}

fun String.asCoreText(): CoreText = CoreText.Dynamic(this)
fun @receiver:StringRes Int.asCoreText(vararg args: Any): CoreText = CoreText.Resource(this, args.toList())

/**
 * UI image/icon resource abstraction decoupling ViewModels from Android drawables.
 */
@Immutable
sealed interface CoreDrawable {
    data class Resource(@param:DrawableRes val resId: Int) : CoreDrawable
    data class Vector(val imageVector: ImageVector) : CoreDrawable
}
