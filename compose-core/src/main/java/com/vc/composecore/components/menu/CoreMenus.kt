package com.vc.composecore.components.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

data class CoreMenuItem(
    val title: String,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

@Composable
fun CoreDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<CoreMenuItem>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.title, style = CoreTheme.typography.bodyMedium) },
                onClick = {
                    item.onClick()
                    onDismissRequest()
                },
                enabled = item.enabled,
                leadingIcon = item.icon?.let {
                    { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(20.dp)) }
                }
            )
        }
    }
}

@Composable
fun CorePopupMenu(
    items: List<CoreMenuItem>,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.MoreVert,
    contentDescription: String = "More options"
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
        CoreDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            items = items
        )
    }
}

@Composable
fun CoreContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<CoreMenuItem>,
    modifier: Modifier = Modifier
) {
    CoreDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        items = items,
        modifier = modifier
    )
}
