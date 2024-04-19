package cz.ondrejmarz.taborak.data.models

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DayPlan(
    val dayId: String? = null,
    val day: String? = null,

    var wakeUp: Activity = Activity("Budíček", type = "Milník"),
    var warmUp: Activity = Activity("Rozcvička", type = "Milník"),
    var summon: Activity = Activity("Nástup", type = "Milník"),
    var prepForNight: Activity = Activity("Příprava na večerku", type = "Milník"),
    var lightsOut: Activity = Activity("Večerka", type = "Milník"),

    var programMorning: Activity = Activity(type = "Dopolední činnost"),
    var programAfternoon: Activity = Activity(type = "Odpolední činnost"),
    var programEvening: Activity = Activity(type = "Podvečerní činnost"),
    var programNight: Activity = Activity(type = "Večerní činnost"),

    // when filling in menu, only @param desc in these is needed to be filled
    var dishBreakfast: Activity = Activity("Snídaně", type = "Jídlo"),
    var dishMorningSnack: Activity = Activity("Svačina", type = "Jídlo"),
    var dishLunch: Activity = Activity("Oběd", type = "Jídlo"),
    var dishAfternoonSnack: Activity = Activity("Svačina", type = "Jídlo"),
    var dishDinner: Activity = Activity("Večeře", type = "Jídlo"),
    var dishEveningSnack: Activity = Activity("Svačina", type = "Jídlo"),

    val getActivityList: List<Activity> =
        listOf(wakeUp, warmUp, summon, prepForNight, lightsOut, programMorning, programAfternoon, programEvening, programNight,
            dishBreakfast, dishMorningSnack, dishLunch, dishAfternoonSnack, dishDinner, dishEveningSnack
        )
)