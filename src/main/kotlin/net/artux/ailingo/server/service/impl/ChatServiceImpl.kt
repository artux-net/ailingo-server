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
import net.artux.ailingo.server.util.AiServiceException
import net.artux.ailingo.server.util.CoinOperationException
import net.artux.ailingo.server.util.InvalidRequestException
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class ChatServiceImpl(
    private val baseChatClient: ChatClient,
    private val topicRepository: TopicRepository,
    private val historyRepository: MessageHistoryRepository,
    private val userService: UserService
) : ChatService {

    private val logger = LoggerFactory.getLogger(ChatServiceImpl::class.java)

    override fun startConversation(topicName: String): ConversationMessageDto {
        val topic = topicRepository.findByName(topicName)
            .orElseThrow { InvalidRequestException("Topic $topicName not found") }

        val user = userService.getCurrentUser()

        if (topic.price > 0) {
            logger.info("Topic '${topic.name}' costs ${topic.price} coins. Checking balance for user ${user.login}.")
            if (user.coins < topic.price) {
                logger.warn("User ${user.login} has insufficient coins (${user.coins}) to start topic '${topic.name}' (cost: ${topic.price}).")
                throw InvalidRequestException("Not enough coins. Topic price: ${topic.price}, but you have: ${user.coins}.")
            }

            val coinResponse = userService.changeCoinsForCurrentUser(-topic.price)
            if (!coinResponse.success) {
                logger.error("Failed to deduct ${topic.price} coins from user ${user.login} for topic '${topic.name}'. Reason: ${coinResponse.message}")
                throw CoinOperationException("Failed to deduct coins: ${coinResponse.message}")
            }
            logger.info("Successfully deducted ${topic.price} coins from user ${user.login} for starting topic '${topic.name}'. New balance: ${coinResponse.newBalance}")
        } else {
            logger.info("Topic '${topic.name}' is free. No coins deducted for user ${user.login}.")
        }

        val conversationId = UUID.randomUUID()
        val initialMessageContent = createMessage(topic, emptyList(), null)?.text
        if (initialMessageContent == null) {
            logger.error("Failed to generate initial welcome message for topic '{}'", topic.name)
            throw AiServiceException("Failed to generate Welcome Prompt.")
        }

        val historyMessage = HistoryMessageEntity().apply {
            this.topic = topic
            this.conversationId = conversationId
            this.content = initialMessageContent
            this.type = MessageType.SYSTEM
            this.owner = user
            this.user = user
            this.timestamp = Instant.now()
        }

        val savedMessage = historyRepository.save(historyMessage)
        logger.info("Started conversation ${savedMessage.conversationId} for user ${user.login} on topic '${topic.name}'.")
        return mapHistoryMessageEntityToConversationMessageDto(savedMessage)
    }

    override fun continueDialog(chatId: UUID, userInput: String): ConversationMessageDto {
        val user = userService.getCurrentUser()
        val messages = historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, user)

        if (messages.isEmpty()) {
            logger.warn("Attempted to continue non-existent or unauthorized conversation {} for user {}", chatId, user.login)
            throw InvalidRequestException("Conversation not found or access denied.")
        }

        val topic = messages.first().topic

        if (messages.last().type == MessageType.FINAL) {
            logger.info("Attempted to continue already finished conversation {} for user {}", chatId, user.login)
            return mapHistoryMessageEntityToConversationMessageDto(messages.last())
        }

        historyRepository.save(
            HistoryMessageEntity().apply {
                this.topic = topic
                this.conversationId = chatId
                this.content = userInput
                this.owner = user
                this.user = user
                this.type = MessageType.USER
                this.timestamp = Instant.now()
            }
        )

        val updatedMessages = historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, user)
        val promptMessages = updatedMessages.map { mapHistoryMessageToMessage(it) }


        val savedAiMessage: HistoryMessageEntity = if (updatedMessages.size < topic.messageLimit) {
            val aiResponse = createMessage(topic, promptMessages, userInput)
            historyRepository.save(
                HistoryMessageEntity().apply {
                    this.topic = topic
                    this.conversationId = chatId
                    this.content = aiResponse?.text
                    this.owner = user
                    this.type = MessageType.ASSISTANT
                    this.user = user
                    this.timestamp = Instant.now()
                }
            )
        } else {
            logger.info("Conversation {} for user {} reached message limit ({}) for topic '{}'. Ending conversation.", chatId, user.login, topic.messageLimit, topic.name)
            val systemMessageToStop = SystemMessage(STOP_CONVERSATION_PROMPT)
            val finalAiResponse = createMessage(topic, promptMessages + systemMessageToStop, userInput)

            val finalHistoryMessage = historyRepository.save(
                HistoryMessageEntity().apply {
                    this.topic = topic
                    this.conversationId = chatId
                    this.content = finalAiResponse?.text
                    this.owner = user
                    this.type = MessageType.FINAL
                    this.user = user
                    this.timestamp = Instant.now()
                }
            )

            try {
                userService.changeUserStreak()
                logger.info("Updated streak for user {} after ending conversation {}", user.login, chatId)
            } catch (e: Exception) {
                logger.error("Failed to update streak for user {} after ending conversation {}: {}", user.login, chatId, e.message, e)
            }
            finalHistoryMessage
        }

        return mapHistoryMessageEntityToConversationMessageDto(savedAiMessage)
    }


    override fun getMessages(chatId: UUID): List<ConversationMessageDto> {
        val user = userService.getCurrentUser()
        val messages = historyRepository.findByConversationIdAndOwnerOrderByTimestamp(chatId, user)
        if (messages.isEmpty()) {
            logger.warn("No messages found or access denied for conversation {} and user {}", chatId, user.login)
            return emptyList()
        }
        return messages.map { mapHistoryMessageEntityToConversationMessageDto(it) }
    }

    override fun getConversations(): MutableList<ConversationDto> {
        return historyRepository.findAllByOwner(userService.getCurrentUser())
    }

    private fun createMessage(topic: TopicEntity, messages: List<Message>, userInput: String?): AssistantMessage? {
        val chatClient = baseChatClient.mutate()
            .defaultSystem(topic.systemPrompt)
            .defaultOptions(getOptions())
            .build()

        val promptToSend = if (userInput != null) {
            Prompt(messages + listOf(UserMessage(userInput)))
        } else {
            Prompt(listOf(SystemMessage(topic.welcomePrompt)))
        }

        return try {
            chatClient.prompt(promptToSend)
                .call()
                .chatResponse()
                ?.result
                ?.output
        } catch (e: Exception) {
            logger.error("Error calling AI model for topic '{}': {}", topic.name, e.message, e)
            null
        }
    }


    private fun mapHistoryMessageToMessage(historyMessage: HistoryMessageEntity): Message {
        return when (historyMessage.type) {
            MessageType.SYSTEM -> SystemMessage(historyMessage.content ?: "")
            MessageType.USER -> UserMessage(historyMessage.content ?: "")
            MessageType.ASSISTANT -> AssistantMessage(historyMessage.content ?: "")
            MessageType.FINAL -> AssistantMessage(historyMessage.content ?: "")
            null -> {
                logger.warn("History message ID {} has null type, defaulting to SystemMessage.", historyMessage.id)
                SystemMessage(historyMessage.content ?: "")
            }
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

    private fun getOptions() = DefaultChatOptionsBuilder()
        .maxTokens(200)
        .temperature(0.7)
        .build()

    companion object {
        const val STOP_CONVERSATION_PROMPT = "Politely inform the user that the conversation message limit for this topic has been reached and you must now conclude the discussion. Wish them well."
    }

    protected fun mapHistoryMessageEntityToConversationMessageDto(historyMessageEntity: HistoryMessageEntity): ConversationMessageDto {
        return ConversationMessageDto(
            historyMessageEntity.id?.toString() ?: "",
            historyMessageEntity.conversationId?.toString() ?: "",
            historyMessageEntity.content ?: "",
            historyMessageEntity.timestamp ?: Instant.now(),
            historyMessageEntity.type ?: MessageType.SYSTEM
        )
    }
}