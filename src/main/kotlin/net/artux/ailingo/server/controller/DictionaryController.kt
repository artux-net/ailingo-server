package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.model.DictionaryResponse
import net.artux.ailingo.server.service.DictionaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Словарь")
@RestController
@RequestMapping("/api/v1/dictionary")
class DictionaryController(
    private val dictionaryService: DictionaryService
) {

    @Operation(summary = "Получение описания введенного слова")
    @GetMapping("/define")
    fun defineWord(@RequestParam word: String): ResponseEntity<List<DictionaryResponse>> {
        val definitions = dictionaryService.getWordDefinition(word)
        return ResponseEntity.ok(definitions)
    }
}