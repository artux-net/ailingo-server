package net.artux.ailingo.server.model.dictionary

data class Definition(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example: String?
)