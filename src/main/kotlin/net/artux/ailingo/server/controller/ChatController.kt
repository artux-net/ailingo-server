package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.model.ConversationDto
import net.artux.ailingo.server.model.ConversationMessageDto
import net.artux.ailingo.server.model.PromptRequest
import net.artux.ailingo.server.service.ChatService
import net.artux.ailingo.server.util.InvalidRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Взаимодействие с ботом")
@RestController
@RequestMapping("/api/v1/conversations")
class ChatController(
    private val chatService: ChatService
) {
    @GetMapping
    fun getTopics(): List<ConversationDto> {
        return chatService.getConversations()
    }

    @PostMapping("/{topicName}")
    fun startConversation(@PathVariable topicName: String): ConversationMessageDto {
        return chatService.startConversation(topicName)
    }

    @Operation(
        summary = "Получить сообщения конкретного диалога",
        description = "Возвращает все сообщения для указанного ID диалога, принадлежащего текущему пользователю."
    )
    @GetMapping("/{conversationId}")
    fun getConversationMessages(@PathVariable conversationId: String): List<ConversationMessageDto> {
        try {
            val conversationUuid = UUID.fromString(conversationId)
            return chatService.getMessages(conversationUuid)
        } catch (e: IllegalArgumentException) {
            throw InvalidRequestException("Invalid conversation ID format.")
        }
    }

    @PostMapping("/continue/{conversationId}")
    fun continueDialog(@PathVariable conversationId: String, @RequestBody userInput: String): ConversationMessageDto {
        return chatService.continueDialog(UUID.fromString(conversationId), userInput)
    }

    @Operation(
        summary = "Отправка произвольного сообщения",
        description = "Не использовать в продакшене, только для тестирования и отладки"
    )
    @PostMapping("/testMessage")
    fun testPrompt(promptRequest: PromptRequest): String {
        return chatService.testPrompt(promptRequest)
    }
}
