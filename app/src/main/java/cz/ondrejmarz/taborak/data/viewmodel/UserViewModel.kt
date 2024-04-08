package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import kotlinx.serialization.SerializationException

class UserViewModel : ViewModel() {

    private val _role = MutableLiveData<UserRole>()
    val role: LiveData<UserRole> = _role

    private val _members = MutableLiveData<List<UserData>>()
    val members: LiveData<List<UserData>> = _members

    private val _applications = MutableLiveData<List<UserData>>()
    val appliactions: LiveData<List<UserData>> = _applications

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAllUsersFromTour(tourId: String) {
        ApiClient.fetchAllMembers(tourId, { responseBody: String ->
            val userList = createUserList(responseBody)
            _members.postValue(userList)
        })
        ApiClient.fetchAllApplications(tourId, { responseBody: String ->
            val userList = createUserList(responseBody)
            _applications.postValue(userList)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkUser(user: UserData?) {
        if (user != null)
            ApiClient.saveUser(user, { })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptApplication(tourId: String, userId: String?) {
        if (userId != null)
            ApiClient.addTourMember(tourId, userId, { fetchAllUsersFromTour(tourId) })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteMember(tourId: String, userId: String) {
        ApiClient.deleteTourMember(tourId, userId, { fetchAllUsersFromTour(tourId) })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendApplication(tourId: String, userId: String?) {
        if (userId != null)
            ApiClient.addTourApplication(tourId, userId, { })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteApplication(tourId: String, userId: String) {
        ApiClient.deleteTourApplication(tourId, userId, { fetchAllUsersFromTour(tourId) })
    }

    fun loadTourUserRole(tourId: String, userId: String) {
        ApiClient.getRole(tourId, userId, { responseBody: String ->
            _role.postValue( UserRole.fromString(responseBody))
        })
    }

    private fun createUserList(responseBody: String): List<UserData> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTourRole(tourId: String, userId: String, role: UserRole) {
        ApiClient.setTourRole(tourId, userId, role.role, {
            fetchAllUsersFromTour(tourId)
            loadTourUserRole(tourId, userId)
        })
    }
}