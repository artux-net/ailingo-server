package org.ailingo.server.model

import java.time.Instant
import java.util.*

data class UserDto(
    val id: UUID,
    val login: String,
    val avatar: String,
    val xp: Int = 0,
    val registration: Instant,
    val lastLoginAt: Instant
) {

}