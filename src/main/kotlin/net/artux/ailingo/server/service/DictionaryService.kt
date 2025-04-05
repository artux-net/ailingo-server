package net.artux.ailingo.server.service

import net.artux.ailingo.server.model.DictionaryResponse

interface DictionaryService {
    fun getWordDefinition(word: String): List<DictionaryResponse>
}