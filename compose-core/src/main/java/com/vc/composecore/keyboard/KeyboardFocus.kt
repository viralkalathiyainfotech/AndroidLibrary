package com.vc.composecore.keyboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.Dp

/**
 * Encapsulated controller providing unified keyboard and focus operations.
 */
class CoreKeyboardController(
    private val keyboardController: SoftwareKeyboardController?,
    private val focusManager: FocusManager
) {
    fun showKeyboard() {
        keyboardController?.show()
    }

    fun hideKeyboard() {
        keyboardController?.hide()
    }

    fun clearFocus(force: Boolean = true) {
        focusManager.clearFocus(force = force)
        hideKeyboard()
    }

    fun focusNext(): Boolean {
        return focusManager.moveFocus(FocusDirection.Next)
    }

    fun focusPrevious(): Boolean {
        return focusManager.moveFocus(FocusDirection.Previous)
    }

    fun focusDown(): Boolean {
        return focusManager.moveFocus(FocusDirection.Down)
    }

    fun focusUp(): Boolean {
        return focusManager.moveFocus(FocusDirection.Up)
    }
}

@Composable
fun rememberCoreKeyboardController(): CoreKeyboardController {
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    return remember(keyboard, focus) { CoreKeyboardController(keyboard, focus) }
}

/**
 * Returns true if the soft keyboard is currently open and occupying height.
 */
@Composable
fun isKeyboardVisible(): Boolean {
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    return imeInsets.asPaddingValues(density).calculateBottomPadding().value > 0
}

/**
 * Returns soft keyboard height in Dp.
 */
@Composable
fun keyboardHeight(): Dp {
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    return imeInsets.asPaddingValues(density).calculateBottomPadding()
}

/**
 * Applies safe IME padding to keep active input fields above the virtual keyboard.
 */
fun Modifier.imePaddingSafe(): Modifier = this.imePadding()

/**
 * Modifier that applies padding matching keyboard appearance dynamically.
 */
fun Modifier.keyboardAware(): Modifier = this.composed {
    val kbHeight = keyboardHeight()
    padding(bottom = kbHeight)
}
