package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MiddleDarkButton(
    onClickButton: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClickButton,
        modifier = Modifier
            .padding(bottom = 20.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}