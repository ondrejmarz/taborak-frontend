package cz.ondrejmarz.taborak.ui.viewmodels.states

import cz.ondrejmarz.taborak.data.models.Group

data class ParticipantsState(
    val groupList: List<Group>? = null,
    val isLoading: Boolean = false
)