package cz.ondrejmarz.taborak.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

@Serializable
data class UserData(
    val userId: String,
    val userName: String?,
    val email: String?,
    val role: UserRole? = UserRole.GUEST
)

@Serializable
enum class UserRole(val role: String) {
    @SerialName("major")
    MAJOR("major"),
    @SerialName("minor")
    MINOR("minor"),
    @SerialName("troop")
    TROOP("troop"),
    @SerialName("guest")
    GUEST("guest"),
    @SerialName("null")
    ERROR("null");

    companion object {
        fun fromString(role: String): UserRole {
            return when (role) {
                "major" -> MAJOR
                "minor" -> MINOR
                "troop" -> TROOP
                "guest" -> GUEST
                else -> ERROR
            }
        }
    }
}