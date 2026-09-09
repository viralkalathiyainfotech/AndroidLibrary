package com.vc.composecore.components.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme
import kotlinx.coroutines.delay

/**
 * State hook for debouncing search queries.
 */
@Composable
fun rememberDebouncedSearch(
    initialQuery: String = "",
    debounceMs: Long = 350L,
    onQueryDebounced: (String) -> Unit
): Pair<String, (String) -> Unit> {
    var query by remember { mutableStateOf(initialQuery) }

    LaunchedEffect(query) {
        delay(debounceMs)
        onQueryDebounced(query)
    }

    return query to { newQuery -> query = newQuery }
}

@Composable
fun CoreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    isLoading: Boolean = false,
    onSearch: (String) -> Unit = {},
    leadingIcon: ImageVector = Icons.Default.Search,
    suggestions: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(CoreRadius.large),
            color = CoreTheme.colors.surface,
            shadowElevation = CoreTheme.elevation.level1,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, style = CoreTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "Search",
                        tint = CoreTheme.colors.outline
                    )
                },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = CoreTheme.colors.primary
                        )
                    } else if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = CoreTheme.colors.outline
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(CoreRadius.large),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoreTheme.colors.primary,
                    unfocusedBorderColor = CoreTheme.colors.outlineVariant
                )
            )
        }

        if (suggestions != null && query.isNotEmpty()) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
            suggestions()
        }
    }
}

@Composable
fun CoreFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = CoreTheme.typography.labelMedium) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CoreTheme.colors.primaryContainer,
            selectedLabelColor = CoreTheme.colors.onPrimaryContainer
        ),
        shape = RoundedCornerShape(CoreRadius.small)
    )
}

@Composable
fun <T> CoreFilterBar(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm)
    ) {
        items.forEach { item ->
            CoreFilterChip(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                label = labelProvider(item)
            )
        }
    }
}
