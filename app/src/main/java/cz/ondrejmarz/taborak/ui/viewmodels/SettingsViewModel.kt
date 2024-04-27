package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.ui.viewmodels.states.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class SettingsViewModel(
    private val tourId: String,
    private val userId: String
) : ViewModel() {

    private val _role = MutableStateFlow(UserRole.ERROR)
    val role: StateFlow<UserRole> = _role.asStateFlow()

    private val _users = MutableStateFlow(UserState())
    val users: StateFlow<UserState> = _users.asStateFlow()

    init {
        fetchAllUsersFromTour()
        loadTourUserRole()
    }

    private fun fetchAllUsersFromTour() {
        viewModelScope.launch {
            ApiClient.fetchAllMembers(tourId) { responseBody: String ->
                _users.update { userState ->
                    userState.copy(
                        members = createUserList(responseBody)
                    )
                }
            }
        }

        viewModelScope.launch {
            ApiClient.fetchAllApplications(tourId) { responseBody: String ->
                _users.update { userState ->
                    userState.copy(
                        application = createUserList(responseBody)
                    )
                }
            }
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

    fun deleteTour() {
        viewModelScope.launch {
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

    fun isLast(role: UserRole): Boolean {
        users.value.members.forEach { user ->
            if (user.userId != userId && user.role == role)
                return false
        }
        return true
    }
}