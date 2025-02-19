package net.artux.ailingo.server.dto

import java.util.*

data class TopicResponseDTO(
    val id: UUID,
    val name: String,
    val imageUrl: String,
    val price: Int
)