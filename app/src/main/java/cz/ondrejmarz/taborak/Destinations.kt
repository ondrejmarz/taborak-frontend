package cz.ondrejmarz.taborak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface AppDestination {
    val icon: ImageVector
    val route: String
}

object Home : AppDestination {
    override val icon = Icons.Filled.FilterList
    override val route = "Rozcestník"
}

object Tour : AppDestination {
    override val icon = Icons.Filled.People
    override val route = "Turnus"
}

object Participants : AppDestination {
    override val icon = Icons.Filled.Groups
    override val route = "Oddíl"
}

object Calendar : AppDestination {
    override val icon = Icons.Filled.CalendarToday
    override val route = "Kalendář"
}

object Tasks : AppDestination {
    override val icon = Icons.Filled.Checklist
    override val route = "Úkoly"
}

object Settings : AppDestination {
    override val icon = Icons.Filled.Settings
    override val route = "Nastavení"
}

// Screens to be displayed in the bottom tab row
val appTabRowScreens = listOf(Tour, Participants, Calendar, Tasks, Settings)