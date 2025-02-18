package net.artux.ailingo.server.dto

data class CreateTopicDTO(
    val name: String,
    val imageUrl: String,
    val price: Int
)