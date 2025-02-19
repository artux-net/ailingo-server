package net.artux.ailingo.server.model

import net.artux.ailingo.server.entity.MessageType
import java.time.Instant

data class ConversationMessageDto(
    val id: String,
    val conversationId: String,
    val content: String,
    val timestamp: Instant,
    val type: MessageType
)
