package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    title: String,
    back: Boolean? = false,
    button: String? = null,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Column (
        modifier = modifier
            .padding(10.dp)
    ) {

        if (back == true && button != null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "<--",
                    color = Color.Blue,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .weight(3f)
                )
                Text(
                    text = button,
                    color = Color.Blue,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .weight(2f)
                )
            }
        }
        if (back != true && button != null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .weight(4f)
                )
                Text(
                    text = button,
                    color = Color.Blue,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .weight(2f)
                )
            }
        }
        if (back == true && button == null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "<--",
                    color = Color.Blue,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .weight(5f)
                )
            }
        }
        if (back != true && button == null) {

            Text(
                text = title,
                style = MaterialTheme.typography.h5,
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
        Section(title = "Test", back = true, button = "Další", modifier = Modifier.fillMaxWidth()) {
            Text(text = "Obsah")
        }
    }
}