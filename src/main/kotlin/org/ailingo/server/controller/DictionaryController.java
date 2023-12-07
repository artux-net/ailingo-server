package org.ailingo.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.ailingo.server.entity.dictionary.Word;
import org.ailingo.server.service.dictionary.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Личный словарь")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    @Autowired
    private WordRepository wordRepository;

    @Operation(summary = "Посмотреть словарь")
    @GetMapping
    public List<Word> getDictionary(UUID userId) {
        return wordRepository.findByUserId(userId);
    }

    @Operation(summary = "Добавить слово")
    @PostMapping("/add-word")
    public Word addWord(@RequestBody Word word) {
        return wordRepository.save(word);
    }

    @Operation(summary = "Изменить слово")
    @PutMapping("/update-word")
    public Word updateWord(Long wordId, @RequestBody Word word) {
        Word existingWord = wordRepository.findById(wordId).orElseThrow(() -> new EntityNotFoundException("Word not found"));
        existingWord.setWord(word.getWord());
        existingWord.setLocale(word.getLocale());
        return wordRepository.save(existingWord);
    }

    @Operation(summary = "Удалить слово")
    @DeleteMapping("/delete-word")
    public void deleteWord(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
