package net.artux.ailingo.server.model.dictionary

data class YandexDef(
    val text: String? = null,
    val pos: String? = null,
    val ts: String? = null,
    val tr: List<YandexTranslation>? = null
)