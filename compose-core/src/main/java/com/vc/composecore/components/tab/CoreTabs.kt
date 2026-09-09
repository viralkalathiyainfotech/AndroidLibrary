package com.vc.composecore.components.tab

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

data class CoreTabData(
    val title: String,
    val icon: ImageVector? = null,
    val badgeCount: Int? = null
)

@Composable
fun CoreTabRow(
    selectedTabIndex: Int,
    tabs: List<CoreTabData>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = CoreTheme.colors.surface,
        contentColor = CoreTheme.colors.primary,
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CoreTheme.colors.primary
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, tabData ->
            CoreTab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                title = tabData.title,
                icon = tabData.icon,
                badgeCount = tabData.badgeCount
            )
        }
    }
}

@Composable
fun CoreScrollableTabRow(
    selectedTabIndex: Int,
    tabs: List<CoreTabData>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    edgePadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp)
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = CoreTheme.colors.surface,
        contentColor = CoreTheme.colors.primary,
        edgePadding = edgePadding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CoreTheme.colors.primary
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, tabData ->
            CoreTab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                title = tabData.title,
                icon = tabData.icon,
                badgeCount = tabData.badgeCount
            )
        }
    }
}

@Composable
fun CoreTab(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    badgeCount: Int? = null,
    enabled: Boolean = true
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = {
            if (badgeCount != null && badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = CoreTheme.colors.error) {
                            Text(if (badgeCount > 99) "99+" else badgeCount.toString())
                        }
                    }
                ) {
                    Text(
                        text = title,
                        style = if (selected) CoreTheme.typography.titleSmall else CoreTheme.typography.bodyMedium,
                        color = if (selected) CoreTheme.colors.primary else CoreTheme.colors.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = title,
                    style = if (selected) CoreTheme.typography.titleSmall else CoreTheme.typography.bodyMedium,
                    color = if (selected) CoreTheme.colors.primary else CoreTheme.colors.onSurfaceVariant
                )
            }
        },
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (selected) CoreTheme.colors.primary else CoreTheme.colors.outline
                )
            }
        }
    )
}
