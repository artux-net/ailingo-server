package net.artux.ailingo.server.dto

import lombok.Builder
import lombok.Data

@Data
@Builder
data class UpdateUserProfileDto(
    val name: String?,
    val email: String?,
    val avatar: String?,
    val newPassword: String?,
    val oldPassword: String?,
)
