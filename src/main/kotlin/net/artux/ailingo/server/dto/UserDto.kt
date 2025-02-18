package net.artux.ailingo.server.dto

import java.time.Instant

data class UserDto(
    val id: Long?,
    val login: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String? = null,
    val xp: Int = 0,
    val coins: Int = 0,
    val streak: Int = 0,
    val registration: Instant?,
    val lastLoginAt: Instant? = null,
    val isEmailVerified: Boolean = false,
)