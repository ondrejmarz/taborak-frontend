package cz.ondrejmarz.taborak.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.auth.SignInResult
import cz.ondrejmarz.taborak.auth.SignInState
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.data.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _role = MutableStateFlow("error")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {

        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun checkIfUserIsInDatabase(user: UserData?) {
        viewModelScope.launch {
            user?.let {
                ApiClient.saveUser(it) { }
            }
        }
    }

    fun loadTourUserRole(tourId: String, userId: String) {
        viewModelScope.launch {
            ApiClient.getRole(tourId, userId) { responseBody: String ->
                _role.update {
                    responseBody
                }
            }
        }
    }
}