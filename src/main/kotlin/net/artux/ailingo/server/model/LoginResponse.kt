package net.artux.ailingo.server.model

import net.artux.ailingo.server.dto.UserDto

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto,
    val isStreakContinued: Boolean
)