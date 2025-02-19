package net.artux.ailingo.server.model

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.openai.OpenAiChatOptions

data class PromptRequest(
    val systemPrompt: String,
    val userInput: String?,
    val messages: List<Message>? = null,
    val chatOptions: OpenAiChatOptions? = null,
)
