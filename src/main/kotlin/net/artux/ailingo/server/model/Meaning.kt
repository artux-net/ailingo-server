package net.artux.ailingo.server.model

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>
)