package net.artux.ailingo.server.controller

import net.artux.ailingo.server.service.WordsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
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