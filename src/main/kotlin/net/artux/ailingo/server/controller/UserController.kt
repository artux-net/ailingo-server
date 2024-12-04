package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.model.RegisterUserDto
import net.artux.ailingo.server.model.Status
import net.artux.ailingo.server.model.UserDto
import net.artux.ailingo.server.model.login.LoginRequest
import net.artux.ailingo.server.model.login.LoginResponse
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenRequest
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenResponse
import net.artux.ailingo.server.model.register.RegisterResponse
import net.artux.ailingo.server.service.ResetService
import net.artux.ailingo.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Пользователь")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val resetService: ResetService
) {

    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUser: RegisterUserDto): ResponseEntity<RegisterResponse> {
        val response = userService.registerUser(registerUser)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Авторизация")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = userService.login(request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Обновление JWT токена")
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val response = userService.refreshToken(request)
        return ResponseEntity.ok(response)
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