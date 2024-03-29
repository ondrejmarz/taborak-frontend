package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    title: String,
    button: String? = null,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Column (
        modifier = modifier
            .padding(10.dp)
    ) {
        if (button != null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .weight(4f)
                )
                Text(
                    text = button,
                    color = Color.Blue,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .weight(2f)
                )
            }
        }
        else {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 20.dp)
            )
        }

        content()
    }
}

@Preview
@Composable
fun PreviewSection() {
    MaterialTheme {
        Section(title = "Test", button = "Další", modifier = Modifier.fillMaxWidth()) {
            Text(text = "Obsah")
        }
    }
}