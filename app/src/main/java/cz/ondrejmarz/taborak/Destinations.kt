package cz.ondrejmarz.taborak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.ramcosta.composedestinations.spec.Direction
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.ui.screens.destinations.CalendarScreenDestination
import cz.ondrejmarz.taborak.ui.screens.destinations.HomeScreenDestination
import cz.ondrejmarz.taborak.ui.screens.destinations.ParticipantsScreenDestination
import cz.ondrejmarz.taborak.ui.screens.destinations.SettingsScreenDestination
import cz.ondrejmarz.taborak.ui.screens.destinations.TasksScreenDestination
import cz.ondrejmarz.taborak.ui.screens.destinations.TourScreenDestination

interface AppDestination {
    val icon: ImageVector
    val route: String
    fun destination(param: Long? = null): Direction
}

object Home : AppDestination {
    override val icon = Icons.Filled.FilterList
    override val route = "Rozcestník"
    override fun destination(param: Long?): Direction {
        return HomeScreenDestination
    }
}

object Tour : AppDestination {
    override val icon = Icons.Filled.People
    override val route = "Turnus"
    override fun destination(param: Long?): Direction {
        if (param != null)
            return TourScreenDestination.invoke(param)
        return HomeScreenDestination
    }
}

object Participants : AppDestination {
    override val icon = Icons.Filled.Groups
    override val route = "Oddíl"
    override fun destination(param: Long?): Direction {
        if (param != null)
            return ParticipantsScreenDestination.invoke(param)
        return HomeScreenDestination
    }
}

object Calendar : AppDestination {
    override val icon = Icons.Filled.CalendarToday
    override val route = "Kalendář"
    override fun destination(param: Long?): Direction {
        if (param != null)
            return CalendarScreenDestination.invoke(param)
        return HomeScreenDestination
    }
}

object Tasks : AppDestination {
    override val icon = Icons.Filled.Checklist
    override val route = "Úkoly"
    override fun destination(param: Long?): Direction {
        if (param != null)
            return TasksScreenDestination.invoke(param)
        return HomeScreenDestination
    }
}

object Settings : AppDestination {
    override val icon = Icons.Filled.Settings
    override val route = "Nastavení"
    override fun destination(param: Long?): Direction {
        if (param != null)
            return SettingsScreenDestination.invoke(param)
        return HomeScreenDestination
    }
}

// Screens to be displayed in the bottom tab row
val appTabRowScreens = listOf(Tour, Participants, Calendar, Tasks, Settings)