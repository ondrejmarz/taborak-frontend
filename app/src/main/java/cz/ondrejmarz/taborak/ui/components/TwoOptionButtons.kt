package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TwoOptionButtons(
    onLeftClick: () -> Unit,
    onLeftText: String,
    onRightClick: () -> Unit,
    onRightText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.small,
            onClick = {
                onLeftClick()
            }
        ) {
            Text(text = onLeftText)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            shape = MaterialTheme.shapes.small,
            onClick = {
                onRightClick()
            }
        ) {
            Text(text = onRightText)
        }
    }
}

@Preview
@Composable
fun TwoOptionButtonPreview() {
    TwoOptionButtons({}, "Zrušit", {}, "Potvrdit")
}