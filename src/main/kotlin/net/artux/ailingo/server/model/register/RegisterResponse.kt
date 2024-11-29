package net.artux.ailingo.server.model.register

import net.artux.ailingo.server.model.UserDto

data class RegisterResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)