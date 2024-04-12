package cz.ondrejmarz.taborak.data.viewmodel.states

import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole

data class UserState(
    // role of currently logged in user, ui is based on this
    val role: UserRole = UserRole.ERROR,
    // list of users that are part of the tour
    val members: List<UserData> = emptyList(),
    // list of users that are applying to join the tour
    val application: List<UserData> = emptyList()
)