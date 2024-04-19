package cz.ondrejmarz.taborak.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val groupId: String,
    val number: String,
    val participants: List<Participant>
)