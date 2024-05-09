package cz.ondrejmarz.taborak.data.util

import cz.ondrejmarz.taborak.auth.UserRole
fun roleToText(role: UserRole?): String {
    return when (role) {
        UserRole.MAJOR -> "Hlavní vedoucí"
        UserRole.MINOR -> "Zástupce"
        UserRole.TROOP -> "Oddílák"
        UserRole.GUEST -> "Ostatní"
        else -> "Chyba"
    }
}