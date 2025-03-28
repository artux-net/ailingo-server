package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.service.WordsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Избранные слова")
@RequestMapping("/api/v1/words")
class WordsController(private val wordsService: WordsService) {

    @PostMapping("/favorites/{word}")
    fun addWordToFavorites(@PathVariable word: String): ResponseEntity<Void> {
        wordsService.addWordToFavorites(word)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/favorites/{word}")
    fun removeWordFromFavorites(@PathVariable word: String): ResponseEntity<Void> {
        wordsService.removeWordFromFavorites(word)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/favorites")
    fun getFavoriteWords(): ResponseEntity<List<String>> {
        val favoriteWords = wordsService.getFavoriteWords()
        return ResponseEntity.ok(favoriteWords)
    }
}
