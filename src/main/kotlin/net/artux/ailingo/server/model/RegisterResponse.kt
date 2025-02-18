package net.artux.ailingo.server.model

import net.artux.ailingo.server.dto.UserDto

data class RegisterResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto?
)