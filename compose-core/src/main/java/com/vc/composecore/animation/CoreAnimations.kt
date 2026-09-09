package com.vc.composecore.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.components.loading.CoreCircularProgress
import com.vc.composecore.theme.CoreAnimationDuration

enum class AnimationStyle {
    Fade,
    FadeAndScale,
    FadeAndSlide,
    Expand
}

@Composable
fun CoreAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    style: AnimationStyle = AnimationStyle.FadeAndScale,
    durationMs: Int = CoreAnimationDuration.normal,
    content: @Composable () -> Unit
) {
    val enter: EnterTransition = when (style) {
        AnimationStyle.Fade -> fadeIn(animationSpec = tween(durationMs, easing = FastOutSlowInEasing))
        AnimationStyle.FadeAndScale -> fadeIn(tween(durationMs)) + scaleIn(tween(durationMs), initialScale = 0.92f)
        AnimationStyle.FadeAndSlide -> fadeIn(tween(durationMs)) + slideInVertically(tween(durationMs)) { it / 3 }
        AnimationStyle.Expand -> expandVertically(tween(durationMs)) + fadeIn(tween(durationMs))
    }

    val exit: ExitTransition = when (style) {
        AnimationStyle.Fade -> fadeOut(animationSpec = tween(durationMs, easing = FastOutSlowInEasing))
        AnimationStyle.FadeAndScale -> fadeOut(tween(durationMs)) + scaleOut(tween(durationMs), targetScale = 0.92f)
        AnimationStyle.FadeAndSlide -> fadeOut(tween(durationMs)) + slideOutVertically(tween(durationMs)) { it / 3 }
        AnimationStyle.Expand -> shrinkVertically(tween(durationMs)) + fadeOut(tween(durationMs))
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

@Composable
fun <T> CoreAnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMs: Int = CoreAnimationDuration.normal,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            (fadeIn(animationSpec = tween(durationMs)) +
                    scaleIn(animationSpec = tween(durationMs), initialScale = 0.95f))
                .togetherWith(
                    fadeOut(animationSpec = tween(durationMs)) +
                            scaleOut(animationSpec = tween(durationMs), targetScale = 0.95f)
                )
        },
        label = "CoreAnimatedContentTransition"
    ) { state ->
        content(state)
    }
}

/**
 * Reusable animation view abstraction. Isolates animation implementations
 * (e.g. Lottie, Rive, Canvas, custom Composables) behind a clean contract.
 */
@Composable
fun CoreAnimationView(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    customAnimation: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (customAnimation != null) {
            customAnimation()
        } else {
            CoreCircularProgress(size = size / 2)
        }
    }
}
