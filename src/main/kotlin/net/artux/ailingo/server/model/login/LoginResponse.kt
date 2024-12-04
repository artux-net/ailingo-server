package net.artux.ailingo.server.model.login

import net.artux.ailingo.server.model.UserDto

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)