package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.util.roleToText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignedBottomSheet(
    state: SheetState,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.small,
        onDismissRequest = onDismiss,
        sheetState = state,
        windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionBottomSheet(
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    DesignedBottomSheet(
        state = sheetState, onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            UserRole.values().forEach { userRole ->
                if (userRole != UserRole.ERROR) {
                    MiddleDarkButton(onClickButton = {
                        onRoleSelected(userRole)
                        onDismiss()
                    }) {
                        Text(text = roleToText(userRole))
                    }
                }
            }
        }
    }
}