package net.artux.ailingo.server.service

import net.artux.ailingo.server.model.ConversationDto
import net.artux.ailingo.server.model.ConversationMessageDto
import net.artux.ailingo.server.model.PromptRequest
import java.util.UUID

interface ChatService {

    fun startConversation(topicName: String): ConversationMessageDto

    fun continueDialog(chatId: UUID, userInput: String): ConversationMessageDto

    fun getMessages(chatId: UUID): List<ConversationMessageDto>

    fun getConversations(): List<ConversationDto>

    fun testPrompt(promptRequest: PromptRequest): String
}
