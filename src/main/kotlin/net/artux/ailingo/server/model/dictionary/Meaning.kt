package net.artux.ailingo.server.model.dictionary

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>
)