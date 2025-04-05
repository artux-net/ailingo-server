package net.artux.ailingo.server.service

import net.artux.ailingo.server.dto.LeaderboardDto

interface LeaderboardService {

    fun getLeaderboard(): List<LeaderboardDto>

}
