package net.artux.ailingo.server.model

import java.time.Instant

data class StreakCheckResult(
    val isStreakContinued: Boolean,
    val streakValidUntil: Instant?
)