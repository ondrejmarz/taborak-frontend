package cz.ondrejmarz.taborak.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.viewmodels.states.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException

class MemberViewModel(private val tourId: String, private val userId: String) : ViewModel() {

    private val _users = MutableStateFlow(UserState())
    val users: StateFlow<UserState> = _users.asStateFlow()

    private val _role = MutableStateFlow(UserRole.ERROR)
    val role: StateFlow<UserRole> = _role.asStateFlow()

    init {
        fetchAllUsersFromTour()
        loadTourUserRole()
    }

    private fun fetchAllUsersFromTour() {
        viewModelScope.launch {
            _users.update { userState -> userState.copy( isLoadingMembers = true ) }
            ApiClient.fetchAllMembers(tourId) { responseBody: String ->
                _users.update { userState ->
                    userState.copy(
                        members = createUserList(responseBody)
                    )
                }
            }
            _users.update { userState -> userState.copy( isLoadingMembers = false ) }
        }

        viewModelScope.launch {
            _users.update { userState -> userState.copy( isLoadingApplications = true ) }
            ApiClient.fetchAllApplications(tourId) { responseBody: String ->
                _users.update { userState ->
                    userState.copy(
                        applications = createUserList(responseBody)
                    )
                }
            }
            _users.update { userState -> userState.copy( isLoadingApplications = false ) }
        }
    }

    fun acceptApplication(userId: String) {
        viewModelScope.launch {
            ApiClient.addTourMember(tourId, userId) { fetchAllUsersFromTour() }
        }
    }

    fun deleteMember(userId: String) {
        viewModelScope.launch {
            ApiClient.deleteTourMember(tourId, userId) { fetchAllUsersFromTour() }
        }
    }

    fun deleteApplication(tourId: String, userId: String) {
        viewModelScope.launch {
            ApiClient.deleteTourApplication(tourId, userId) { fetchAllUsersFromTour() }
        }
    }

    fun setTourRole(userId: String, role: UserRole) {
        viewModelScope.launch {
            ApiClient.setTourRole(tourId, userId, role.role) {
                fetchAllUsersFromTour()
            }
        }.invokeOnCompletion {
            // when user changed own role
            if (this.userId == userId)
                loadTourUserRole()
        }
    }

    private fun loadTourUserRole() {
        viewModelScope.launch {
            ApiClient.getRole(tourId, userId) { responseBody: String ->
                _role.update {
                    UserRole.fromString(responseBody)
                }
            }
        }
    }

    fun isLastMajor(): Boolean {
        var oneFound = false
        users.value.members.forEach { user ->
            if (user.role == UserRole.MAJOR) {
                if (oneFound)
                    return false
                else
                    oneFound = true
            }
        }
        return true
    }

    fun deleteTour() {
        runBlocking {
            ApiClient.deleteTour(
                tourId,
                onSuccess = {  },
                onFailure = { e: IOException -> println(e.message) }
            )
        }
    }

    private fun createUserList(responseBody: String): List<UserData> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }
}