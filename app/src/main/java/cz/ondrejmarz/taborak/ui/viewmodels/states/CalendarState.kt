package cz.ondrejmarz.taborak.ui.viewmodels.states

import cz.ondrejmarz.taborak.data.models.DayPlan

data class CalendarState(
    val dayProgram: DayPlan? = null,
    val isLoading: Boolean = false
    // val menu: Menu = Menu()
    // val duty: Duty = Duty()
)