package org.ailingo.server.model

import java.time.Instant
import java.util.*

data class UserDto(
    val id: UUID,
    val login: String = "",
    val avatar: String = "",
    val xp: Int = 0,
    val coins: Int = 0,
    val streak: Int = 0,
    val registration: Instant,
    val lastLoginAt: Instant = Instant.now(),
) {

}