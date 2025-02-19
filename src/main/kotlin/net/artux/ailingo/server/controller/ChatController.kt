package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.model.ConversationDto
import net.artux.ailingo.server.model.PromptRequest
import net.artux.ailingo.server.service.ChatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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
    fun startConversation(@PathVariable topicName: String): String {
        return chatService.startConversation(topicName).toString()
    }

    @PostMapping("/dialog/{chatId}")
    fun continueDialog(@PathVariable chatId: String, @RequestBody userInput: String) {
        chatService.continueDialog(UUID.fromString(chatId), userInput)
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