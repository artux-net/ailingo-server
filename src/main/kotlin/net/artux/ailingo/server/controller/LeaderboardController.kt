package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.dto.LeaderboardDto
import net.artux.ailingo.server.service.LeaderboardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Список пользователей")
@RestController
@RequestMapping("api/v1/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {
    @Operation(summary = "Получение списка пользователей, сортированного по страйку")
    @GetMapping
    fun getLeaderboard(): List<LeaderboardDto> {
        return leaderboardService.getLeaderboard()
    }
}