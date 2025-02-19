package net.artux.ailingo.server.service.impl

import jakarta.transaction.Transactional
import net.artux.ailingo.server.entity.HistoryMessageEntity
import net.artux.ailingo.server.entity.MessageType
import net.artux.ailingo.server.entity.TopicEntity
import net.artux.ailingo.server.model.ConversationDto
import net.artux.ailingo.server.model.ConversationMessageDto
import net.artux.ailingo.server.model.PromptRequest
import net.artux.ailingo.server.repository.MessageHistoryRepository
import net.artux.ailingo.server.repository.TopicRepository
import net.artux.ailingo.server.service.ChatService
import net.artux.ailingo.server.service.UserService
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class ChatServiceImpl(
    private val baseChatClient: ChatClient,
    private val topicRepository: TopicRepository,
    private val historyRepository: MessageHistoryRepository,
    private val userService: UserService
) : ChatService {

    override fun startConversation(topicName: String): ConversationMessageDto {
        val topic = topicRepository.findByName(topicName).orElseThrow()
        val historyMessage = HistoryMessageEntity().apply {
            this.topic = topic
            this.conversationId = UUID.randomUUID()
            this.content = createMessage(topic, emptyList(), null)?.text
            this.type = MessageType.SYSTEM
            this.owner = userService.getCurrentUser()
        }

        return mapHistoryMessageEntityToConversationMessageDto(historyRepository.save(historyMessage))
    }

    override fun continueDialog(chatId: UUID, userInput: String): ConversationMessageDto {
        val user = userService.getCurrentUser()
        val messages = historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, user)
        val topic = messages.firstOrNull()?.topic ?: throw IllegalStateException("Can not find topic for conversation")
        val promptMessages = messages.map { mapHistoryMessageToMessage(it) }

        historyRepository.save(
            HistoryMessageEntity().apply {
                this.topic = topic
                this.conversationId = chatId
                this.content = userInput
                this.owner = user
                this.type = MessageType.USER
            }
        )

        val message = if (messages.size < topic.messageLimit) {
            val message = createMessage(topic, promptMessages, userInput)
            historyRepository.save(
                HistoryMessageEntity().apply {
                    this.topic = topic
                    this.conversationId = chatId
                    this.content = message?.text
                    this.owner = user
                    this.type = MessageType.ASSISTANT
                }
            )
        } else {
            val systemMessageToStop = SystemMessage(STOP_CONVERSATION_PROMPT)
            val message = createMessage(topic, promptMessages + systemMessageToStop, userInput)
            historyRepository.save(
                HistoryMessageEntity().apply {
                    this.topic = topic
                    this.conversationId = chatId
                    this.content = message?.text
                    this.owner = user
                    this.type = MessageType.FINAL
                }
            )
        }

        return mapHistoryMessageEntityToConversationMessageDto(message)
    }

    override fun getMessages(chatId: UUID): List<ConversationMessageDto> {
        return historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, userService.getCurrentUser())
            .map {
                mapHistoryMessageEntityToConversationMessageDto(it)
            }
    }

    override fun getConversations(): MutableList<ConversationDto> {
        return historyRepository.findAllByOwner(userService.getCurrentUser())
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

        return chatClient.prompt(prompt)
            .call()
            .chatResponse()
            ?.result
            ?.output
    }

    private fun mapHistoryMessageToMessage(historyMessage: HistoryMessageEntity): Message {
        return when (historyMessage.type) {
            MessageType.SYSTEM -> SystemMessage(historyMessage.content)
            MessageType.USER -> UserMessage(historyMessage.content)
            MessageType.ASSISTANT -> AssistantMessage(historyMessage.content)
            MessageType.FINAL -> SystemMessage(historyMessage.content)
            else -> throw IllegalArgumentException("Unknown message type")
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

    // TODO take from topic
    private fun getOptions(topic: TopicEntity) = DefaultChatOptionsBuilder().maxTokens(200).build()

    companion object {
        const val STOP_CONVERSATION_PROMPT = "Now you have to stop the conversation and leave, DO NOT TELL ANYONE more"
    }

    protected fun mapHistoryMessageEntityToConversationMessageDto(historyMessageEntity: HistoryMessageEntity): ConversationMessageDto {
        return ConversationMessageDto(
            historyMessageEntity.id.toString(),
            historyMessageEntity.conversationId.toString(),
            historyMessageEntity.content,
            historyMessageEntity.timestamp,
            historyMessageEntity.type
        )
    }
}
