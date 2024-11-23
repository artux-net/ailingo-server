package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.TopicResponseDTO
import net.artux.ailingo.server.service.TopicService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Общий список топиков")
@RestController
@RequestMapping("/api/v1/topics/")
class TopicsController(
    private val topicService: TopicService
) {

    @Operation(summary = "Получение топиков")
    @GetMapping("/getTopics")
    fun getTopics(@RequestParam locale: String): List<TopicResponseDTO> {
        return topicService.getTopics(locale)
    }

    @Operation(summary = "Добавление нового топика")
    @PostMapping("/addTopic")
    fun addTopic(@RequestBody topic: TopicEntity): ResponseEntity<String> {
        topicService.addTopic(topic)
        return ResponseEntity.status(HttpStatus.CREATED).body("Topic added successfully")
    }

    @Operation(summary = "Удаление топика по названию")
    @DeleteMapping("/deleteTopic")
    fun deleteTopicByName(@RequestParam name: String): ResponseEntity<String> {
        topicService.deleteTopicByName(name)
        return ResponseEntity.status(HttpStatus.OK).body("Topic deleted successfully")
    }

    @Operation(summary = "Удаление топика по ID")
    @DeleteMapping("/deleteTopic/{id}")
    fun deleteTopic(@PathVariable id: Long): ResponseEntity<String> {
        topicService.deleteTopicById(id)
        return ResponseEntity.status(HttpStatus.OK).body("Topic deleted successfully")
    }
}