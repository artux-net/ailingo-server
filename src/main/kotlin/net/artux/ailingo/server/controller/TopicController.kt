package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.dto.CreateTopicDTO
import net.artux.ailingo.server.dto.TopicResponseDTO
import net.artux.ailingo.server.dto.UpdateTopicDTO
import net.artux.ailingo.server.service.TopicService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Общий список топиков")
@RequestMapping("/api/v1/topics/")
@RestController
class TopicController(
    private val topicService: TopicService
) {
    @Operation(summary = "Получение топиков")
    @GetMapping("/getTopics")
    private fun getTopics(): ResponseEntity<List<TopicResponseDTO>> {
        val topics = topicService.getTopics()
        return ResponseEntity(topics, HttpStatus.OK)
    }

    @Operation(summary = "Добавление нового топика")
    @PostMapping("/addTopic")
    fun addTopic(@RequestBody createTopicDTO: CreateTopicDTO): ResponseEntity<String> {
        topicService.addTopic(createTopicDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body("Топик успешно добавлен")
    }

    @Operation(summary = "Добавление новых топиков")
    @PostMapping("/addTopics")
    fun addTopics(@RequestBody createTopicDTOs: List<CreateTopicDTO>): ResponseEntity<String> {
        topicService.addTopics(createTopicDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body("Топики успешно добавлены")
    }

    @Operation(summary = "Обновление топика")
    @PutMapping("/updateTopic/{id}")
    fun updateTopic(@PathVariable id: Long, @RequestBody updateTopicDTO: UpdateTopicDTO): ResponseEntity<String> {
        topicService.updateTopic(id, updateTopicDTO)
        return ResponseEntity.status(HttpStatus.OK).body("Топик успешно обновлен")
    }

    @Operation(summary = "Удаление топика по названию")
    @DeleteMapping("/deleteTopic")
    fun deleteTopicByName(@RequestParam name: String): ResponseEntity<String> {
        topicService.deleteTopicByName(name)
        return ResponseEntity.status(HttpStatus.OK).body("Топик $name удален")
    }

    @Operation(summary = "Удаление топика по ID")
    @DeleteMapping("/deleteTopic/{id}")
    fun deleteTopic(@PathVariable id: Long): ResponseEntity<String> {
        topicService.deleteTopicById(id)
        return ResponseEntity.status(HttpStatus.OK).body("Топик $id удален")
    }
}