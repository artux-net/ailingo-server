package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.ailingo.server.model.RegisterUserDto
import org.ailingo.server.model.Status
import org.ailingo.server.model.UserDto
import org.ailingo.server.service.user.UserService
import org.ailingo.server.service.user.reset.ResetService
import org.springframework.web.bind.annotation.*
import java.util.*


@Tag(name = "Пользователь")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val resetService: ResetService,
) {
    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUser: RegisterUserDto?): Status {
        return userService.registerUser(registerUser)
    }

    @Operation(summary = "Основная информация")
    @GetMapping("/info")
    fun loginUser(): UserDto {
        return userService.getUserDto()
    }

    @PutMapping("/reset/pass")
    @Operation(summary = "Запрос на сброс пароля")
    fun sendResetPasswordLetter(@RequestParam("email") email: String?): Status {
        return resetService.sendResetPasswordLetter(email)
    }

    @PostMapping("/addCoins")
    @Operation(summary = "Добавить монеты пользователю")
    fun addCoinsToCurrentUser(@RequestParam("amount") amount: Int): Status {
        userService.addCoinsToCurrentUser(amount)
        return Status(true, "Монеты успешно добавлены.")
    }

    @PostMapping("/removeCoins")
    @Operation(summary = "Вычесть монеты у пользователя")
    fun removeCoinsFromCurrentUser(@RequestParam("amount") amount: Int): Status {
        userService.removeCoinsFromCurrentUser(amount)
        return Status(true, "Монеты успешно убавлены.")
    }

    @Operation(summary = "Обновление профиля пользователя")
    @PutMapping("/updateProfile")
    fun updateUserProfile(
        @RequestParam(name = "name") name: String,
        @RequestParam(name = "email") email: String,
        @RequestParam(name = "avatar") avatar: String?
    ): Status {
        return userService.updateUserProfile(name, email, avatar)
    }

    @Operation(summary = "Изменение пароля пользователя")
    @PutMapping("/changePassword")
    fun changePassword(
        @RequestParam("oldPassword") oldPassword: String,
        @RequestParam("newPassword") newPassword: String
    ): Status {
        return userService.changePassword(oldPassword, newPassword)
    }
}