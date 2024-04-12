package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.viewmodel.states.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException

class UserViewModel : ViewModel() {

    private val _users = MutableStateFlow(UserState())
    val users: StateFlow<UserState> = _users.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAllUsersFromTour(tourId: String) {

        ApiClient.fetchAllMembers(tourId) { responseBody: String ->
            _users.update { userState ->
                userState.copy(
                    members = createUserList(responseBody)
                )
            }
        }

        ApiClient.fetchAllApplications(tourId) { responseBody: String ->
            _users.update { userState ->
                userState.copy(
                    application = createUserList(responseBody)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkIfUserIsInDatabase(user: UserData?) {
        user?.let {
            ApiClient.saveUser(it) { }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptApplication(tourId: String, userId: String?) {
        userId?.let {
            ApiClient.addTourMember(tourId, it) { fetchAllUsersFromTour(tourId) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteMember(tourId: String, userId: String) {
        ApiClient.deleteTourMember(tourId, userId, { fetchAllUsersFromTour(tourId) })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendApplication(tourId: String, userId: String?) {
        userId?.let {
            ApiClient.addTourApplication(tourId, it) { }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteApplication(tourId: String, userId: String) {
        ApiClient.deleteTourApplication(tourId, userId) { fetchAllUsersFromTour(tourId) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTourRole(tourId: String, userId: String, role: UserRole) {
        ApiClient.setTourRole(tourId, userId, role.role) {
            fetchAllUsersFromTour(tourId)
            loadTourUserRole(tourId, userId)
        }
    }

    fun loadTourUserRole(tourId: String, userId: String) {
        ApiClient.getRole(tourId, userId) { responseBody: String ->
            _users.update { userState ->
                userState.copy(
                    role = UserRole.fromString(responseBody)
                )
            }
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