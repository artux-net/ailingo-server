package net.artux.ailingo.server.model

data class DictionaryResponse(
    val word: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>
)