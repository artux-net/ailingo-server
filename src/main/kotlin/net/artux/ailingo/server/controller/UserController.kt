package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.model.RegisterUserDto
import net.artux.ailingo.server.model.Status
import net.artux.ailingo.server.model.UserDto
import net.artux.ailingo.server.service.UserService
import net.artux.ailingo.server.service.ResetService
import org.springframework.web.bind.annotation.*


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

    @PostMapping("/changeCoins")
    @Operation(summary = "Смена кол-ва монет пользователю")
    fun changeCoinsForCurrentUser(@RequestParam("amount") amount: Int): Status {
        val status = userService.changeCoinsForCurrentUser(amount)
        return status
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