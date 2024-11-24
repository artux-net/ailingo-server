package net.artux.ailingo.server.model

import org.springframework.ai.chat.messages.MessageType
import java.time.Instant

data class ConversationMessageDto(
    val id: String,
    val content: String,
    val timestamp: Instant,
    val type: MessageType
)