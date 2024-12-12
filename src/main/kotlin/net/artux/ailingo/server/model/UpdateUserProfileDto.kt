package net.artux.ailingo.server.model


import lombok.Builder
import lombok.Data

@Data
@Builder
data class UpdateUserProfileDto(
    val name: String?,
    val email: String?,
    val avatar: String?
)