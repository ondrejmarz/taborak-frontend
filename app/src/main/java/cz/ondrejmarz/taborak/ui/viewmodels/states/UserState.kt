package cz.ondrejmarz.taborak.ui.viewmodels.states

import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole

data class UserState(
    // list of users that are part of the tour
    val members: List<UserData> = emptyList(),
    // list of users that are applying to join the tour
    val application: List<UserData> = emptyList()
)