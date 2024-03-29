package cz.ondrejmarz.taborak.auth

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)