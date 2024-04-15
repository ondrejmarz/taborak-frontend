package cz.ondrejmarz.taborak.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Activity(

    val name: String? = "Bez názvu",
    val type: String? = null,
    val desc: String? = null,

    val visible: Boolean? = true,

    val startTime: String? = null,
    val endTime: String? = null,
)