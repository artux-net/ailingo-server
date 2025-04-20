package net.artux.ailingo.server.model.dictionary

data class YandexDictionaryResponse(
    val def: List<YandexDef>? = null,
    val nmt_code: Int? = null,
    val code: Int? = null
)