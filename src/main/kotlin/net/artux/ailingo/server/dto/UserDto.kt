package net.artux.ailingo.server.dto

import net.artux.ailingo.server.model.Role
import java.time.Instant
import java.util.UUID

data class UserDto(
    val id: UUID?,
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
    val role: Role,
    val lastStreakAt: Instant?
)
