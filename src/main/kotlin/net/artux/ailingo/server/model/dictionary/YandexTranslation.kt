package net.artux.ailingo.server.model.dictionary

data class YandexTranslation(
    val text: String? = null,
    val pos: String? = null,
    val gen: String? = null,
    val fr: Int? = null,
    val syn: List<YandexSynonym>? = null,
    val mean: List<YandexMean>? = null
)