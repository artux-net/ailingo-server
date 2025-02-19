package net.artux.ailingo.server.dto

import java.util.UUID

data class TopicResponseDTO(
    val id: UUID,
    val name: String,
    val imageUrl: String,
    val price: Int
)
