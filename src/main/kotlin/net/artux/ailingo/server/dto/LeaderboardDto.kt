package net.artux.ailingo.server.dto



data class LeaderboardDto(
    val coins: Int = 0,
    val streak: Int = 0,
    val avatar: String? = null,
    val name: String = "",
)