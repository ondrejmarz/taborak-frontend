package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.spec.Direction
import cz.ondrejmarz.taborak.AppDestination
import cz.ondrejmarz.taborak.appTabRowScreens
import java.util.Locale

@Composable
fun BottomNavBar(
    tourId: String,
    allScreens: List<AppDestination>,
    onItemSelected: (String) -> Unit,
    currentScreen: String
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .selectableGroup()
                .padding(bottom = bottomPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            allScreens.forEach { screen ->
                AppTab(
                    text = screen.route,
                    icon = screen.icon,
                    onSelected = {
                        if (currentScreen != screen.name)
                            onItemSelected( screen.route + "/" + tourId )
                    },
                    selected = currentScreen == screen.name
                )
            }
        }
    }
}

@Composable
private fun AppTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    Box(modifier = Modifier
        .selectable(selected, true, onClick = onSelected)
        .padding(20.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(tourId = "", allScreens = appTabRowScreens, onItemSelected = { }, currentScreen = "Kalendář")
}
