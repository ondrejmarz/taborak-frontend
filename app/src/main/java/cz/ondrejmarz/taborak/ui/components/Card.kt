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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputDayString
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputTimeString

@Composable
fun DesignedCard(
    title: String,
    topic: String? = null,
    description: String? = null,
    startTime: String? = null,
    endTime: String? = null,
    timeInDayFormat: Boolean? = true,
    enabled: Boolean? = null,
    button: String? = null,
    onClickAction: (() -> Unit)? = null,
) {
    Card(
        onClick = { onClickAction?.invoke() },
        enabled = onClickAction != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        colors = CardColors(
            containerColor = if (enabled == true) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled == true) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.small

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    //color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(9f)
                )

                var tourTime: String? = null
                if (timeInDayFormat == true)
                    tourTime = dayText(startTime, endTime)
                if (timeInDayFormat == false)
                    tourTime = timeText(startTime, endTime)

                if ( tourTime != null ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = tourTime,
                        //color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(2f)
                    )
                }
            }

            if (endTime == null) {
                Spacer(modifier = Modifier.height(5.dp))
            }

            if (topic != null) {
                Text(text = topic, style = MaterialTheme.typography.bodyMedium)
            }

            if (description != null) {
                if (topic != null)
                    Spacer(modifier = Modifier.height(5.dp))

                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }

            if (onClickAction != null && button != null) {

                Text(
                    //color = MaterialTheme.colorScheme.secondary,
                    text = button,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Preview
@Composable
fun DesignedCardPreview() {
    Column {
        DesignedCard(
            title = "Náhled jedné karty",
            topic = "Komponenta",
            description = "Jen se koukni jak vypadám",
            enabled = true,
            button = "Tlačítko",
            onClickAction = {},
        )
        DesignedCard(
            title = "Náhled druhé karty",
            topic = "Komponenta",
            description = "Proč jsem neaktivní? :(",
            enabled = false,
            button = "Tlačítko",
            onClickAction = {},
        )
    }
}

private fun dayText(startDate: String?, endDate: String?): String? {

    val startTime = formatDateStringToOutputDayString(startDate)
    val endTime = formatDateStringToOutputDayString(endDate)

    var timeString: String? = null
    if (startTime != null && endTime != null) timeString = "od $startTime do $endTime"
    if (startTime != null && endTime == null) timeString = "od $startTime"
    if (startTime == null && endTime != null) timeString = "do $endTime"

    return timeString
}

private fun timeText(startDate: String?, endDate: String?): String? {

    val startTime = formatDateStringToOutputTimeString(startDate)
    val endTime = formatDateStringToOutputTimeString(endDate)

    var timeString: String? = null
    if (startTime != null && endTime != null) timeString = "od $startTime do $endTime"
    if (startTime != null && endTime == null) timeString = "od $startTime"
    if (startTime == null && endTime != null) timeString = "do $endTime"

    return timeString
}