package cz.ondrejmarz.taborak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cz.ondrejmarz.taborak.ui.screens.*

interface AppDestination {
    val icon: ImageVector
    val route: String
}

object Overview : AppDestination {
    override val icon = Icons.Filled.FilterList
    override val route = "overview"
}

object Tour : AppDestination {
    override val icon = Icons.Filled.People
    override val route = "tour"
}

object Participants : AppDestination {
    override val icon = Icons.Filled.Groups
    override val route = "participants"
}

object Schedule : AppDestination {
    override val icon = Icons.Filled.CalendarToday
    override val route = "schedule"
}

object Tasks : AppDestination {
    override val icon = Icons.Filled.Checklist
    override val route = "tasks"
}

object Settings : AppDestination {
    override val icon = Icons.Filled.Settings
    override val route = "settings"
}

// Screens to be displayed in the bottom tab row
val appTabRowScreens = listOf(Tour, Participants, Schedule, Tasks, Settings)