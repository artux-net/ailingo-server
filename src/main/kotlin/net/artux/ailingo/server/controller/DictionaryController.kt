package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.model.CombinedDictionaryResponse
import net.artux.ailingo.server.service.DictionaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Словарь")
@RestController
@RequestMapping("/api/v1/dictionary")
class DictionaryController(
    private val dictionaryService: DictionaryService
) {
    @Operation(summary = "Получение описания введенного слова")
    @GetMapping("/define")
    fun defineWord(@RequestParam word: String): ResponseEntity<CombinedDictionaryResponse> { // Изменено на CombinedDictionaryResponse
        val definitions = dictionaryService.getWordDefinition(word)
        return ResponseEntity.ok(definitions)
    }
}