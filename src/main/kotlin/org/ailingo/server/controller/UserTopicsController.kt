package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.ailingo.server.model.Status
import org.ailingo.server.service.user.UserService
import org.ailingo.server.entity.TopicEntity
import org.springframework.web.bind.annotation.*


@Tag(name = "Сохранные топики")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/topics")
class UserTopicsController(
    private val userService: UserService
) {
    @Operation(summary = "Получение сохраненных топиков пользователя")
    @GetMapping("/favourites")
    fun getUserSavedTopics(): Set<TopicEntity> {
        return userService.getUserSavedTopics()
    }

    @Operation(summary = "Сохранение топиков пользователя")
    @PostMapping("/save")
    fun saveUserTopics(@RequestBody topics: Set<TopicEntity>): Status {
        userService.saveUserTopics(topics)
        return Status(true, "Сохранено успешно.")
    }

    @Operation(summary = "Удаление топика пользователя")
    @DeleteMapping("/remove")
    fun removeUserTopic(@RequestBody topic: TopicEntity): Status {
        userService.removeUserTopic(topic)
        return Status(true, "Удалено успешно.")
    }
}