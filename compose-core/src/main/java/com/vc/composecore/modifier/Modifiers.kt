package com.vc.composecore.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

/**
 * Clickable modifier enforcing a debounce window to prevent rapid multi-taps and duplicate API invocations.
 */
fun Modifier.debouncedClickable(
    debounceMs: Long = 600L,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role
    ) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= debounceMs) {
            lastClickTime = now
            onClick()
        }
    }
}

/**
 * Ensures an action is only executed once within the debounce threshold.
 */
fun Modifier.singleClick(
    debounceMs: Long = 800L,
    onClick: () -> Unit
): Modifier = debouncedClickable(debounceMs = debounceMs, onClick = onClick)

/**
 * Clickable modifier with no ripple or indication effect.
 */
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}

/**
 * Conditionally applies a modifier modification chain.
 */
inline fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) then(modifier(Modifier)) else this
}

/**
 * Fills max width if the condition holds true.
 */
fun Modifier.fillMaxWidthIf(condition: Boolean): Modifier = conditional(condition) {
    fillMaxWidth()
}

/**
 * Toggles visibility via alpha and touch handling without restructuring the layout node tree.
 */
fun Modifier.visibleIf(visible: Boolean): Modifier = this.then(
    Modifier.alpha(if (visible) 1f else 0f)
)

/**
 * Applies padding only if condition evaluates to true.
 */
fun Modifier.paddingIf(condition: Boolean, padding: Dp): Modifier = conditional(condition) {
    padding(padding)
}

/**
 * Conditionally attaches a clickable action.
 */
fun Modifier.clickableIf(condition: Boolean, onClick: () -> Unit): Modifier = conditional(condition) {
    debouncedClickable(onClick = onClick)
}

/**
 * Conditionally clips with shape.
 */
fun Modifier.clipIf(condition: Boolean, shape: Shape): Modifier = conditional(condition) {
    clip(shape)
}

/**
 * Conditionally paints a background color.
 */
fun Modifier.backgroundIf(condition: Boolean, color: Color): Modifier = conditional(condition) {
    background(color)
}

/**
 * Centralized test tag modifier for UI automation and semantics assertions.
 */
fun Modifier.coreTestTag(tag: String): Modifier = this.testTag(tag)
