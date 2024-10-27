package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.model.Status
import net.artux.ailingo.server.service.user.UserService
import org.springframework.web.bind.annotation.*

@Tag(name = "Сохраненные слова")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/words")
class UserWordsController(
    private val userService: UserService
) {
    @PostMapping("/add")
    @Operation(summary = "Добавить слово в избранное")
    fun addWordToFavorites(@RequestParam("word") word: String): Status {
        return userService.addWordToFavorites(word)
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Удалить слово из избранного")
    fun removeWordFromFavorites(@RequestParam("word") word: String): Status {
        return userService.removeWordFromFavorites(word)
    }

    @GetMapping("/favorites")
    @Operation(summary = "Получить список избранных слов")
    fun getFavoriteWords(): Set<String>? {
        return userService.favoriteWords
    }
}