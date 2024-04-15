package cz.ondrejmarz.taborak.data.models

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DayPlan(
    val dayId: String? = null,
    val day: String? = null,

    val wakeUp: Activity = Activity("Budíček", type = "Milník"),
    val warmUp: Activity = Activity("Rozcvička", type = "Milník"),
    val summon: Activity = Activity("Nástup", type = "Milník"),
    val prepForNight: Activity = Activity("Příprava na večerku", type = "Milník"),
    val lightsOut: Activity = Activity("Večerka", type = "Milník"),

    val programMorning: Activity = Activity(type = "Dopolední činnost"),
    val programAfternoon: Activity = Activity(type = "Odpolední činnost"),
    val programEvening: Activity = Activity(type = "Podvečerní činnost"),
    val programNight: Activity = Activity(type = "Večerní činnost"),

    // when filling in menu, only @param desc in these is needed to be filled
    val dishBreakfast: Activity = Activity("Snídaně", type = "Jídlo"),
    val dishMorningSnack: Activity = Activity("Svačina", type = "Jídlo"),
    val dishLunch: Activity = Activity("Oběd", type = "Jídlo"),
    val dishAfternoonSnack: Activity = Activity("Svačina", type = "Jídlo"),
    val dishDinner: Activity = Activity("Večeře", type = "Jídlo"),
    val dishEveningSnack: Activity = Activity("Svačina", type = "Jídlo"),

    val getActivityList: List<Activity> =
        listOf(wakeUp, warmUp, summon, prepForNight, lightsOut, programMorning, programAfternoon, programEvening, programNight,
            dishBreakfast, dishMorningSnack, dishLunch, dishAfternoonSnack, dishDinner, dishEveningSnack
        )
)