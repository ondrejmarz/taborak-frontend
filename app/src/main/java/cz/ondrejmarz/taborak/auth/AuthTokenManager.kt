package cz.ondrejmarz.taborak.auth

import android.content.Context
import android.content.SharedPreferences
object AuthTokenManager {
    private const val PREF_NAME = "auth_pref"
    private const val AUTH_TOKEN_KEY = "auth_token"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(token: String) {
        sharedPreferences?.edit()?.putString(AUTH_TOKEN_KEY, token)?.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences?.getString(AUTH_TOKEN_KEY, null)
    }
}