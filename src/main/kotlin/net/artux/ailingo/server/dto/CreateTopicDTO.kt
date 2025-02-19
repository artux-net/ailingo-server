package net.artux.ailingo.server.dto

data class CreateTopicDTO(
    val name: String,
    val image: String,
    val price: Int,
    val level: Int
)