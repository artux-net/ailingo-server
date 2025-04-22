package net.artux.ailingo.server.model

import java.time.Instant

data class ConversationMessageDto(
    val id: String,
    val conversationId: String,
    val content: String?,
    val timestamp: Instant,
    val type: MessageType,
    var suggestions: List<String>? = null
)