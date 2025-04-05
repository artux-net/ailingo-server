package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.dto.LeaderboardDto
import net.artux.ailingo.server.entity.UserEntity
import net.artux.ailingo.server.repository.UserRepository
import net.artux.ailingo.server.service.LeaderboardService
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceImpl(
    private val userRepository: UserRepository
): LeaderboardService {
    override fun getLeaderboard(): List<LeaderboardDto> {
        return userRepository.findTop100ByOrderByStreakDesc().map { getLeaderboard(it) }
    }

    fun getLeaderboard(userEntity: UserEntity): LeaderboardDto {
        return LeaderboardDto(
            userEntity.coins,
            userEntity.streak,
            userEntity.avatar,
            userEntity.name,
        )
    }
}
