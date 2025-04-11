package net.artux.ailingo.server.model

data class StreakCheckResult(
    val streakWasValid: Boolean,
    val needsSave: Boolean
)