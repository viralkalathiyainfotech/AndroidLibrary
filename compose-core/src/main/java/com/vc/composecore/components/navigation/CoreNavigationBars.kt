package com.vc.composecore.components.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

data class CoreNavigationItemData<T>(
    val routeKey: T,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badgeCount: Int? = null,
    val hasBadge: Boolean = false
)

@Composable
fun <T> CoreNavigationBar(
    items: List<CoreNavigationItemData<T>>,
    selectedRouteKey: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = CoreTheme.colors.surface,
        contentColor = CoreTheme.colors.onSurface
    ) {
        items.forEach { item ->
            val isSelected = item.routeKey == selectedRouteKey
            CoreNavigationItem(
                selected = isSelected,
                onClick = { onItemSelected(item.routeKey) },
                icon = if (isSelected && item.selectedIcon != null) item.selectedIcon else item.icon,
                label = item.label,
                badgeCount = item.badgeCount,
                hasBadge = item.hasBadge
            )
        }
    }
}

@Composable
fun RowScope.CoreNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    hasBadge: Boolean = false,
    enabled: Boolean = true
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        icon = {
            if (hasBadge || (badgeCount != null && badgeCount > 0)) {
                BadgedBox(
                    badge = {
                        if (badgeCount != null && badgeCount > 0) {
                            Badge(
                                containerColor = CoreTheme.colors.error,
                                contentColor = CoreTheme.colors.onError
                            ) {
                                Text(if (badgeCount > 99) "99+" else badgeCount.toString())
                            }
                        } else if (hasBadge) {
                            Badge(
                                containerColor = CoreTheme.colors.primary,
                                modifier = Modifier.size(6.dp)
                            )
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = label)
                }
            } else {
                Icon(imageVector = icon, contentDescription = label)
            }
        },
        label = { Text(text = label, style = CoreTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CoreTheme.colors.primary,
            selectedTextColor = CoreTheme.colors.primary,
            indicatorColor = CoreTheme.colors.primaryContainer,
            unselectedIconColor = CoreTheme.colors.outline,
            unselectedTextColor = CoreTheme.colors.onSurfaceVariant
        )
    )
}

@Composable
fun <T> CoreNavigationRail(
    items: List<CoreNavigationItemData<T>>,
    selectedRouteKey: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null
) {
    NavigationRail(
        modifier = modifier,
        containerColor = CoreTheme.colors.surface,
        contentColor = CoreTheme.colors.onSurface,
        header = header
    ) {
        items.forEach { item ->
            val isSelected = item.routeKey == selectedRouteKey
            NavigationRailItem(
                selected = isSelected,
                onClick = { onItemSelected(item.routeKey) },
                icon = {
                    Icon(
                        imageVector = if (isSelected && item.selectedIcon != null) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = CoreTheme.typography.labelSmall) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = CoreTheme.colors.primary,
                    indicatorColor = CoreTheme.colors.primaryContainer
                )
            )
        }
    }
}
