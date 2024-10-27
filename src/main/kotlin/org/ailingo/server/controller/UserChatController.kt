package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.ailingo.server.entity.ChatHistoryEntity
import org.ailingo.server.model.Status
import org.ailingo.server.service.user.UserService
import org.springframework.web.bind.annotation.*


@Tag(name = "История чата с Chat GPT")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
class UserChatController(
    private val userService: UserService
) {
    @Operation(summary = "Получить чаты пользователя")
    @GetMapping("/chats")
    fun getUserChats(): List<ChatHistoryEntity> {
        val currentUser = userService.currentUser
        return userService.getUserChats(currentUser)
    }

    @Operation(summary = "Сохранить чат пользователя")
    @PostMapping("/chat")
    fun saveUserChat(@RequestBody chat: ChatHistoryEntity): Status {
//        val currentUser = userService.currentUser
//        userService.saveUserChat(currentUser, chat)
        return Status(true, "Чат успешно сохранен.")
    }
}