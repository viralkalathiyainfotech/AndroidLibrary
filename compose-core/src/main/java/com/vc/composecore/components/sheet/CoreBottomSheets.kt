package com.vc.composecore.components.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    shape: Shape = RoundedCornerShape(topStart = CoreRadius.xlarge, topEnd = CoreRadius.xlarge),
    containerColor: Color = CoreTheme.colors.surface,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            shape = shape,
            containerColor = containerColor,
            dragHandle = dragHandle
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CoreTheme.spacing.lg, vertical = CoreTheme.spacing.sm),
                content = content
            )
        }
    }
}

@Composable
fun CoreBottomSheet(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(topStart = CoreRadius.xlarge, topEnd = CoreRadius.xlarge),
    containerColor: Color = CoreTheme.colors.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = containerColor,
        shadowElevation = CoreTheme.elevation.level3
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CoreTheme.spacing.lg),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    ),
    sheetPeekHeight: Dp = 64.dp,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = sheetContent,
        modifier = modifier,
        sheetPeekHeight = sheetPeekHeight,
        sheetContainerColor = CoreTheme.colors.surface,
        content = content
    )
}
