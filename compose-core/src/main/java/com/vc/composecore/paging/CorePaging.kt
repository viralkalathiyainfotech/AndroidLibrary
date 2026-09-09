package com.vc.composecore.paging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.vc.composecore.components.loading.CoreCircularProgress
import com.vc.composecore.components.status.CoreEmptyView
import com.vc.composecore.components.status.CoreErrorView
import com.vc.composecore.theme.CoreTheme

@Composable
fun CorePagingLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(CoreTheme.spacing.md),
        contentAlignment = Alignment.Center
    ) {
        CoreCircularProgress(size = 32.dp)
    }
}

@Composable
fun CorePagingError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(CoreTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = CoreTheme.typography.bodySmall,
            color = CoreTheme.colors.error
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun CorePagingFooter(
    loadState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (loadState) {
        is LoadState.Loading -> CorePagingLoading(modifier = modifier)
        is LoadState.Error -> CorePagingError(
            message = loadState.error.localizedMessage ?: "Failed to load more items",
            onRetry = onRetry,
            modifier = modifier
        )
        is LoadState.NotLoading -> {}
    }
}

@Composable
fun <T : Any> CorePagingContent(
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(CoreTheme.spacing.sm),
    emptyContent: @Composable () -> Unit = { CoreEmptyView() },
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CoreCircularProgress()
                }
            }
            pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0 -> {
                val error = (pagingItems.loadState.refresh as LoadState.Error).error
                CoreErrorView(
                    message = error.localizedMessage ?: "Failed to load data",
                    onRetry = { pagingItems.retry() }
                )
            }
            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                emptyContent()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = if (itemKey != null) { index ->
                            pagingItems[index]?.let { itemKey(it) } ?: index
                        } else null
                    ) { index ->
                        val item = pagingItems[index]
                        if (item != null) {
                            itemContent(item)
                        }
                    }

                    item(key = "core_paging_footer") {
                        CorePagingFooter(
                            loadState = pagingItems.loadState.append,
                            onRetry = { pagingItems.retry() }
                        )
                    }
                }
            }
        }
    }
}
