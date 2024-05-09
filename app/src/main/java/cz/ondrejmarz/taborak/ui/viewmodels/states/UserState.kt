package cz.ondrejmarz.taborak.ui.viewmodels.states

import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole

data class UserState(
    // list of users that are part of the tour
    val members: List<UserData> = emptyList(),
    val isLoadingMembers: Boolean = false,

    // list of users that are applying to join the tour
    val applications: List<UserData> = emptyList(),
    val isLoadingApplications: Boolean = false
)