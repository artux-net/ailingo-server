package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.service.SavedTopicService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Сохранные топики")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/topics/favourites")
class SavedTopicsController(
    private val savedTopicService: SavedTopicService
) {
    @Operation(summary = "Получение сохраненных топиков пользователя")
    @GetMapping
    fun getUserSavedTopics(): ResponseEntity<List<TopicEntity>> {
        return ResponseEntity.ok(savedTopicService.getUserSavedTopics())
    }

    @Operation(summary = "Сохранение топиков пользователя")
    @PostMapping
    fun saveUserTopics(@RequestBody topics: List<TopicEntity>): ResponseEntity<Void> {
        savedTopicService.saveUserTopics(topics)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "Удаление топика пользователя")
    @DeleteMapping
    fun removeUserTopic(@RequestBody topic: TopicEntity): ResponseEntity<Void> {
        savedTopicService.removeUserTopic(topic)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
