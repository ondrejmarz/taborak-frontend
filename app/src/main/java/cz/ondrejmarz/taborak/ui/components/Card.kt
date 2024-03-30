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
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputDayString

@Composable
fun DesignedCard(
    title: String,
    topic: String? = null,
    description: String? = null,
    startTime: String? = null,
    endTime: String? = null,
    enabled: Boolean? = null,
    button: String? = null,
    onClickAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(vertical = 10.dp)
            .clickable { onClickAction?.invoke() },
        shape = MaterialTheme.shapes.small,
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (enabled == false) Color.LightGray else MaterialTheme.colorScheme.surfaceContainer)
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.titleMedium
            )

            val tourTime = TimeText(startTime, endTime)

            if ( tourTime != null ) {
                Text(
                    text = tourTime,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }

            if (topic != null) {

                Text(
                    text = topic,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (description != null) {

                Text(
                    text = description,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (enabled != null) {

                Text(
                    color = MaterialTheme.colorScheme.secondary,
                    text = if (button == null) "Otevřít" else button,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}

private fun TimeText(startDate: String?, endDate: String?): String? {

    val startTime = formatDateStringToOutputDayString(startDate)
    val endTime = formatDateStringToOutputDayString(endDate)

    var timeString: String? = null
    if (startTime != null && endTime != null) timeString = startTime + "––" + endTime
    if (startTime != null && endTime == null) timeString = "od " + startTime
    if (startTime == null && endTime != null) timeString = "do " + endTime

    return timeString
}
