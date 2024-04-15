package cz.ondrejmarz.taborak.data.viewmodel.states

import cz.ondrejmarz.taborak.data.models.DayPlan

data class CalendarState(
    val dayProgram: DayPlan? = null
    // val menu: Menu = Menu()
    // val duty: Duty = Duty()
)