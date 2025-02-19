package net.artux.ailingo.server.dto

data class RegisterUserDto(
    val login: String,
    val password: String,
    val email: String,
    val name: String
)
