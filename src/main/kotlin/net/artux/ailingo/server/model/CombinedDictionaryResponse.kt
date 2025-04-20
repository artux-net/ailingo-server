package net.artux.ailingo.server.model

import net.artux.ailingo.server.model.dictionary.DictionaryResponse
import net.artux.ailingo.server.model.dictionary.YandexDictionaryResponse

data class CombinedDictionaryResponse(
    val dictionaryApiDevResponses: List<DictionaryResponse>?,
    val yandexDictionaryResponse: YandexDictionaryResponse?
)