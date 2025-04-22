package net.artux.ailingo.server.service

import net.artux.ailingo.server.model.CombinedDictionaryResponse

interface DictionaryService {
    fun getWordDefinition(word: String): CombinedDictionaryResponse
}