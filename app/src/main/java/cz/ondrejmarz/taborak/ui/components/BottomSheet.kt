package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.auth.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionBottomSheet(
    roles: List<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet (
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vyberte roli:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            MiddleDarkButton(onClickButton = {
                onRoleSelected(UserRole.MAJOR)
                onDismiss()
            }) {
                Text(text = "Hlavní vedoucí")
            }
            MiddleDarkButton(onClickButton = {
                onRoleSelected(UserRole.MINOR)
                onDismiss()
            }) {
                Text(text = "Zástupce")
            }
            MiddleDarkButton(onClickButton = {
                onRoleSelected(UserRole.TROOP)
                onDismiss()
            }) {
                Text(text = "Oddílový vedoucí")
            }
            MiddleDarkButton(onClickButton = {
                onRoleSelected(UserRole.GUEST)
                onDismiss()
            }) {
                Text(text = "Ostatní")
            }
        }
    }
}