package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.material3.Button
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
    onButtonClick: (() -> Unit)? = null,
    buttonTitle: String? = null,
    content: @Composable () -> Unit
) {
    Column (
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(5f)
            )

            if (onButtonClick != null && buttonTitle != null) {

                Button(
                    modifier = Modifier
                        .weight(2f),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onButtonClick() }
                ) {
                    Text(text = buttonTitle)
                }
            }
        }

        content()
    }
}

@Preview
@Composable
fun PreviewSection() {
    MaterialTheme {
        Section(title = "Nadpis", onButtonClick = {}, buttonTitle = "Další") {
            Text(text = "Obsah")
        }
    }
}