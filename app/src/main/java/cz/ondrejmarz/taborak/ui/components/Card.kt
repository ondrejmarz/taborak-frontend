package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DesignedCard(
    title: String,
    topic: String? = null,
    description: String? = null,
    startTime: String? = null,
    endTime: String? = null,
    enabled: Boolean? = true,
    button: String? = null,
    onClickAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(10.dp)
            .clickable { onClickAction?.invoke() },
        shape = MaterialTheme.shapes.small,

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (enabled == false) Color.LightGray else MaterialTheme.colorScheme.surface)
                .padding(10.dp)
                .padding(10.dp)
        ) {
            TitleText(title)
            TimeText(startTime = startTime, endTime = endTime)
            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TitleText(title)
                TimeText(startTime = startTime, endTime = endTime)
            }*/

            if (topic != null) {

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = topic,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (description != null) {

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = description,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (enabled != null) {

                Text(
                    color = MaterialTheme.colorScheme.secondary,
                    text = if (button == null) "Otevřít" else button,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun TitleText(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun TimeText(startTime: String?, endTime: String?) {
    var timeString = ""
    if (startTime != null && endTime != null) timeString = startTime + "––" + endTime
    if (startTime != null && endTime == null) timeString = "od " + startTime
    if (startTime == null && endTime != null) timeString = "do " + endTime
    Text(
        text = timeString,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        style = MaterialTheme.typography.labelSmall,
    )
}
