package cz.ondrejmarz.taborak.data.viewmodel.states

import cz.ondrejmarz.taborak.data.models.Group

data class ParticipantsState(
    val groupList: List<Group>? = null
)