package net.artux.ailingo.server.model

data class CreateTopicDTO(
    val name: String,
    val imageUrl: String,
    val price: Int
)