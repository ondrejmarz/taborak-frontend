package cz.ondrejmarz.taborak.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Participant(

    val name: String? = "Osoba",
    val age: String? = null,

    val parentEmail: String? = null,
    val parentPhone: String? = null
)