package com.vc.composecore.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    crossfade: Boolean = true,
    placeholderIcon: ImageVector = Icons.Default.BrokenImage
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .crossfade(crossfade)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.clip(shape),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CoreTheme.colors.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = CoreTheme.colors.primary
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CoreTheme.colors.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = placeholderIcon,
                    contentDescription = "Failed to load image",
                    tint = CoreTheme.colors.outline
                )
            }
        }
    )
}

@Composable
fun CoreNetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(CoreRadius.small)
) {
    CoreAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        shape = shape
    )
}

@Composable
fun CoreThumbnail(
    model: Any?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    contentDescription: String? = null
) {
    CoreAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .aspectRatio(1f),
        shape = shape
    )
}

enum class AvatarStatus {
    Online,
    Offline,
    Busy,
    Away
}

@Composable
fun CoreAvatar(
    model: Any? = null,
    initials: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    status: AvatarStatus? = null,
    contentDescription: String? = "Avatar"
) {
    Box(modifier = modifier.size(size)) {
        if (model != null) {
            CoreAsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                shape = shape
            )
        } else if (!initials.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(CoreTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials.take(2).uppercase(),
                    style = CoreTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.38f).sp
                    ),
                    color = CoreTheme.colors.onPrimaryContainer
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(CoreTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = contentDescription,
                    tint = CoreTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }

        if (status != null) {
            val statusColor = when (status) {
                AvatarStatus.Online -> CoreTheme.colors.success
                AvatarStatus.Offline -> CoreTheme.colors.outline
                AvatarStatus.Busy -> CoreTheme.colors.error
                AvatarStatus.Away -> CoreTheme.colors.warning
            }
            val indicatorSize = size * 0.28f
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .align(Alignment.BottomEnd)
                    .background(statusColor, CircleShape)
                    .border(1.5.dp, CoreTheme.colors.surface, CircleShape)
            )
        }
    }
}

@Composable
fun CoreAvatarGroup(
    avatars: List<Pair<Any?, String?>>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 4,
    avatarSize: Dp = 40.dp
) {
    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = avatars.size - maxVisible

    Row(modifier = modifier) {
        visibleAvatars.forEachIndexed { index, (model, initials) ->
            Box(modifier = Modifier.offset(x = (-8 * index).dp)) {
                CoreAvatar(
                    model = model,
                    initials = initials,
                    size = avatarSize,
                    modifier = Modifier.border(2.dp, CoreTheme.colors.surface, CircleShape)
                )
            }
        }

        if (remainingCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-8 * visibleAvatars.size).dp)
                    .size(avatarSize)
                    .background(CoreTheme.colors.surfaceVariant, CircleShape)
                    .border(2.dp, CoreTheme.colors.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$remainingCount",
                    style = CoreTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CoreTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
