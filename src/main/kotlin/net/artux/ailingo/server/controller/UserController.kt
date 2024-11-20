package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.model.*
import net.artux.ailingo.server.service.ResetService
import net.artux.ailingo.server.service.UserService
import org.springframework.http.ResponseEntity
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
    fun registerUser(@RequestBody registerUser: RegisterUserDto?): AuthResponse {
        return userService.registerUser(registerUser)
    }

    @Operation(summary = "Авторизация")
    @PostMapping("/login")
    fun authenticate(
        @RequestBody request: AuthRequest?
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(userService.authenticate(request))
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