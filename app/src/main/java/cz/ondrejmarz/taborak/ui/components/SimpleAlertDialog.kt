package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        AlertDialog(
            tonalElevation = 0.dp,
            //containerColor = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TwoOptionButtons(
                    onLeftClick = onDismissRequest,
                    onLeftText = "Zrušit",
                    onRightClick = onConfirmation,
                    onRightText = "Ano"
                )
            }
        )
    }
}

@Preview
@Composable
fun SimpleAlertDialogPreview() {
    SimpleAlertDialog(
        onDismissRequest = { },
        onConfirmation = { },
        dialogTitle = "Opravdu chcete provést tuto akci?",
        dialogText = "Pokud tuto akci provedete, bude mít nenapravitelné následky."
    )
}