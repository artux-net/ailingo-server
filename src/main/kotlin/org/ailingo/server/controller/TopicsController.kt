package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.ailingo.server.topics.TopicEntity
import org.ailingo.server.topics.TopicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@Tag(name = "Общий список топиков")
@RestController
@RequestMapping("/api/v1/topics/")
class TopicsController(
    private val topicRepository: TopicRepository
) {
    @Operation(summary = "Получение топиков")
    @GetMapping("/getTopics")
    fun getResponse(): List<TopicEntity> {
        return topicRepository.findAll()
    }

    @Operation(summary = "Добавление нового топика")
    @PostMapping("/addTopic")
    fun addTopic(@RequestBody topic: TopicEntity): ResponseEntity<String> {
        topicRepository.save(topic)
        return ResponseEntity.status(HttpStatus.CREATED).body("Topic added successfully")
    }

    @Operation(summary = "Удаление топика по названию")
    @DeleteMapping("/deleteTopic")
    fun deleteTopicByName(@RequestParam name: String): ResponseEntity<String> {
        topicRepository.deleteTopicByName(name)
        return ResponseEntity.status(HttpStatus.OK).body("Topic deleted successfully")
    }

    @Operation(summary = "Удаление топика по ID")
    @DeleteMapping("/deleteTopic/{id}")
    fun deleteTopic(@PathVariable id: Long): ResponseEntity<String> {
        topicRepository.deleteTopicById(id)
        return ResponseEntity.status(HttpStatus.OK).body("Topic deleted successfully")
    }
}