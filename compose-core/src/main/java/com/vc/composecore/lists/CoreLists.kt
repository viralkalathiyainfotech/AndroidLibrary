package com.vc.composecore.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.components.divider.CoreDivider
import com.vc.composecore.components.status.CoreEmptyView
import com.vc.composecore.components.status.CoreErrorView
import com.vc.composecore.theme.CoreTheme

@Composable
fun <T> CoreLazyColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(CoreTheme.spacing.sm),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    showSeparators: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { CoreEmptyView() },
    itemContent: @Composable (item: T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CoreTheme.colors.primary)
                }
            }
            errorMessage != null -> {
                CoreErrorView(message = errorMessage, onRetry = onRetry)
            }
            items.isEmpty() -> {
                emptyContent()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement
                ) {
                    if (header != null) {
                        item(key = "core_header") {
                            header()
                        }
                    }

                    itemsIndexed(
                        items = items,
                        key = key?.let { k -> { _, item -> k(item) } }
                    ) { index, item ->
                        itemContent(item)
                        if (showSeparators && index < items.size - 1) {
                            CoreDivider(modifier = Modifier.padding(top = CoreTheme.spacing.xs))
                        }
                    }

                    if (footer != null) {
                        item(key = "core_footer") {
                            footer()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> CoreLazyRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = CoreTheme.spacing.md),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(CoreTheme.spacing.sm),
    itemContent: @Composable (item: T) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        itemsIndexed(
            items = items,
            key = key?.let { k -> { _, item -> k(item) } }
        ) { _, item ->
            itemContent(item)
        }
    }
}

@Composable
fun CoreLazyVerticalGrid(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(CoreTheme.spacing.md),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(CoreTheme.spacing.md),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(CoreTheme.spacing.md),
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier.fillMaxSize(),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun CoreLazyHorizontalGrid(
    rows: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(CoreTheme.spacing.md),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(CoreTheme.spacing.md),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(CoreTheme.spacing.md),
    content: LazyGridScope.() -> Unit
) {
    LazyHorizontalGrid(
        rows = rows,
        modifier = modifier.fillMaxWidth(),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreSwipeableItem(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onArchive: (() -> Unit)? = null,
    deleteIcon: ImageVector = Icons.Default.Delete,
    archiveIcon: ImageVector = Icons.Default.Archive,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDismiss()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onArchive?.invoke()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> CoreTheme.colors.error
                SwipeToDismissBoxValue.StartToEnd -> CoreTheme.colors.primary
                SwipeToDismissBoxValue.Settled -> Color.Transparent
            }
            val alignment = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.Settled -> Alignment.Center
            }
            val icon = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> deleteIcon
                SwipeToDismissBoxValue.StartToEnd -> archiveIcon
                SwipeToDismissBoxValue.Settled -> deleteIcon
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = CoreTheme.spacing.lg),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        content = { content() }
    )
}
