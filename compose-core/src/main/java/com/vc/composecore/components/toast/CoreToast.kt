package com.vc.composecore.components.toast

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.vc.composecore.resources.CoreText
import com.vc.composecore.state.CollectEffect
import com.vc.composecore.state.UiEffect
import kotlinx.coroutines.flow.Flow

/**
 * Toast helper for Jetpack Compose applications without coupling ViewModels to Android Context.
 */
object CoreToast {
    fun show(context: Context, message: String, isLong: Boolean = false) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(context, message, duration).show()
    }

    fun show(context: Context, text: CoreText, isLong: Boolean = false) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(context, text.asString(context), duration).show()
    }
}

/**
 * Composable listener that automatically displays toasts when ViewModel emits [UiEffect.ShowToast].
 */
@Composable
fun HandleToastEffects(effectFlow: Flow<UiEffect>) {
    val context = LocalContext.current
    CollectEffect(flow = effectFlow) { effect ->
        if (effect is UiEffect.ShowToast) {
            CoreToast.show(context, effect.message.asString(context), effect.isLong)
        }
    }
}
