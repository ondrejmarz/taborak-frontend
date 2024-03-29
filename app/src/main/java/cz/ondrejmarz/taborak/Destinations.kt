package cz.ondrejmarz.taborak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface AppDestination {
    val icon: ImageVector
    val name: String
    val route: String
}

object SignIn : AppDestination {
    override val icon = Icons.AutoMirrored.Filled.Login
    override val name = "Přihlášení"
    override val route = "sign_in"
}
object Home : AppDestination {
    override val icon = Icons.Filled.FilterList
    override val name = "Rozcestník"
    override val route = "home"
}

object Tour : AppDestination {
    override val icon = Icons.Filled.People
    override val name = "Turnus"
    override val route = "tour/{tourId}"
}

object Participants : AppDestination {
    override val icon = Icons.Filled.Groups
    override val name = "Účastníci"
    override val route = "participants/{tourId}"
}

object Calendar : AppDestination {
    override val icon = Icons.Filled.CalendarToday
    override val name = "Kalendář"
    override val route = "calendar/{tourId}"
}

object Tasks : AppDestination {
    override val icon = Icons.Filled.Checklist
    override val name = "Úkoly"
    override val route = "tasks/{tourId}"
}

object Settings : AppDestination {
    override val icon = Icons.Filled.Settings
    override val name = "Nastavení"
    override val route = "settings/{tourId}"
}

// Screens to be displayed in the bottom tab row
val appTabRowScreens = listOf(Tour, Participants, Calendar, Tasks, Settings)