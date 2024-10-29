package net.artux.ailingo.server.model

import lombok.Builder
import lombok.Data

@Data
@Builder
data class RegisterUserDto(
    val login: String,
    val password: String,
    val email: String,
    val name: String,
    val avatar: String
)