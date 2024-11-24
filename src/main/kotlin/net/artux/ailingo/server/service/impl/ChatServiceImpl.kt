package net.artux.ailingo.server.service.impl

import net.artux.ailingo.server.entity.HistoryMessageEntity
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.ConversationDto
import net.artux.ailingo.server.model.ConversationMessageDto
import net.artux.ailingo.server.model.PromptRequest
import net.artux.ailingo.server.repositories.MessageHistoryRepository
import net.artux.ailingo.server.repositories.TopicRepository
import net.artux.ailingo.server.service.ChatService
import net.artux.ailingo.server.service.UserService
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.ChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatServiceImpl(
    private val baseChatClient: ChatClient,
    private val topicRepository: TopicRepository,
    private val historyRepository: MessageHistoryRepository,
    private val userService: UserService
) : ChatService {

    override fun startConversation(topicName: String): UUID {
        val topic = topicRepository.findByName(topicName).orElseThrow()
        val historyMessage = HistoryMessageEntity().apply {
            this.topic = topic
            this.conversationId = UUID.randomUUID()
            this.content = createMessage(topic, emptyList(), null)?.content
            this.type = MessageType.SYSTEM
            this.owner = userService.currentUser
        }

        return historyRepository.save(historyMessage).conversationId
    }

    override fun continueDialog(chatId: UUID, userInput: String) {
        val messages = historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, userService.currentUser)
        val topic = messages.firstOrNull()?.topic ?: throw IllegalStateException("Can not find topic for conversation")
        val promptMessages = messages.map { mapHistoryMessageToMessage(it) }

        if (messages.size < topic.messageLimit) {
            val message = createMessage(topic, promptMessages, userInput)
            historyRepository.save(HistoryMessageEntity().apply {
                this.topic = topic
                this.conversationId = chatId
                this.content = message?.content
                this.owner = userService.currentUser
            })
        } else {
            val systemMessageToStop = SystemMessage(STOP_CONVERSATION_PROMPT)
            val message = createMessage(topic, promptMessages + systemMessageToStop, userInput)
            historyRepository.save(HistoryMessageEntity().apply {
                this.topic = topic
                this.conversationId = chatId
                this.content = message?.content
                this.owner = userService.currentUser
            })
        }
    }

    override fun getMessages(chatId: UUID): List<ConversationMessageDto> {
        return historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, userService.currentUser)
            .map {
                ConversationMessageDto(
                    it.id.toString(),
                    it.content,
                    it.timestamp,
                    it.type
                )
            }
    }


    override fun getConversations(): MutableList<ConversationDto> {
        return historyRepository.findAllByOwner(userService.currentUser)
    }

    private fun createMessage(topic: TopicEntity, messages: List<Message>, userInput: String?): AssistantMessage? {
        val chatClient = baseChatClient.mutate()
            .defaultSystem(topic.systemPrompt)
            .defaultOptions(getOptions(topic))
            .build()

        val prompt = if (userInput != null) {
            Prompt(messages + listOf(UserMessage(userInput)))
        } else {
            Prompt(listOf(SystemMessage(topic.welcomePrompt)))
        }

        val result = chatClient.prompt(prompt).call()

        return result.chatResponse()?.result?.output
    }

    private fun mapHistoryMessageToMessage(historyMessage: HistoryMessageEntity): Message {
        return when (historyMessage.type) {
            MessageType.SYSTEM -> SystemMessage(historyMessage.content)
            MessageType.USER -> UserMessage(historyMessage.content)
            MessageType.ASSISTANT -> AssistantMessage(historyMessage.content)
            MessageType.TOOL -> SystemMessage(historyMessage.content)
        }
    }

    override fun testPrompt(promptRequest: PromptRequest): String {
        val builder = baseChatClient.mutate()
            .defaultSystem(promptRequest.systemPrompt)

        if (promptRequest.chatOptions != null) {
            builder.defaultOptions(promptRequest.chatOptions)
        }

        val chatClient = builder.build()
        val request = chatClient.prompt(
            Prompt(
                UserMessage(promptRequest.userInput)
            )
        )

        return request.call().content() ?: throw Exception("Can not get response from OpenAI")
    }

    private fun getOptions(topic: TopicEntity) = ChatOptionsBuilder.builder().withMaxTokens(200).build()

    companion object {
        const val STOP_CONVERSATION_PROMPT = "Now you have to stop the conversation and leave, DO NOT TELL ANYONE more"
    }
}
