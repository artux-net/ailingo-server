package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.ailingo.server.model.RegisterUserDto
import org.ailingo.server.model.Status
import org.ailingo.server.model.UserDto
import org.ailingo.server.service.user.UserService
import org.ailingo.server.service.user.reset.ResetService
import org.ailingo.server.topics.TopicEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@Tag(name = "Пользователь")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val resetService: ResetService,
) {
    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUser: RegisterUserDto?): Status {
        return userService.registerUser(registerUser)
    }

    @Operation(summary = "Основная информация")
    @GetMapping("/info")
    fun loginUser(): UserDto {
        return userService.getUserDto()
    }

    @PutMapping("/reset/pass")
    @Operation(summary = "Запрос на сброс пароля")
    fun sendResetPasswordLetter(@RequestParam("email") email: String?): Status {
        return resetService.sendResetPasswordLetter(email)
    }

    @Operation(summary = "Получение сохраненных топиков пользователя")
    @GetMapping("/topics")
    fun getUserSavedTopics(): Set<TopicEntity> {
        return userService.getUserSavedTopics()
    }

    @Operation(summary = "Сохранение топиков пользователя")
    @PostMapping("/topics/save")
    fun saveUserTopics(@RequestBody topics: Set<TopicEntity>): Status {
        userService.saveUserTopics(topics)
        return Status(true, "Сохранено успешно.")
    }

    @Operation(summary = "Удаление топика пользователя")
    @DeleteMapping("/topics/remove")
    fun removeUserTopic(@RequestBody topic: TopicEntity): Status {
        userService.removeUserTopic(topic)
        return Status(true, "Удалено успешно.")
    }

    @PostMapping("/favorites/add")
    @Operation(summary = "Добавить слово в избранное")
    fun addWordToFavorites(@RequestParam("word") word: String): Status {
        return userService.addWordToFavorites(word)
    }

    @DeleteMapping("/favorites/remove")
    @Operation(summary = "Удалить слово из избранного")
    fun removeWordFromFavorites(@RequestParam("word") word: String): Status {
        return userService.removeWordFromFavorites(word)
    }

    @GetMapping("/favorites")
    @Operation(summary = "Получить список избранных слов")
    fun getFavoriteWords(): Set<String>? {
        return userService.favoriteWords
    }

    @PostMapping("/addCoins")
    @Operation(summary = "Добавить монеты пользователю")
    fun addCoinsToCurrentUser(@RequestParam("amount") amount: Int): Status {
        userService.addCoinsToCurrentUser(amount)
        return Status(true, "Монеты успешно добавлены.")
    }

    @PostMapping("/removeCoins")
    @Operation(summary = "Вычесть монеты у пользователя")
    fun removeCoinsFromCurrentUser(@RequestParam("amount") amount: Int): Status {
        userService.removeCoinsFromCurrentUser(amount)
        return Status(true, "Монеты успешно убавлены.")
    }
}