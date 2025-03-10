package net.artux.ailingo.server.dto

data class CreateTopicDTO(
    val name: String,
    val image: String,
    val price: Int,
    val level: Int,
    val welcomePrompt: String,
    val systemPrompt: String,
    val messageLimit: Int
)