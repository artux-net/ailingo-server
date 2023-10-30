package org.ailingo.server.controller

import org.ailingo.server.service.ChatService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/chat/")
class LlamaController(
    private val chatService: ChatService
) {

    @PostMapping("/message")
    fun getResponse(@RequestBody message: String): String {
        return chatService.getResponse(message)
    }

    @PutMapping("/context")
    fun setContext(@RequestBody context: String) {
        chatService.setContext(context)
    }

    @PutMapping("/user-role")
    fun setUserRole(@RequestParam role: String) {
        chatService.setUserRole(role)
    }

    @PutMapping("/ai-role")
    fun setAIRole(@RequestParam role: String) {
        chatService.setAIRole(role)
    }

}