package cz.ondrejmarz.taborak.auth

import kotlinx.serialization.Serializable

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

@Serializable
data class UserData(
    val userId: String,
    val userName: String?,
    val email: String?
)