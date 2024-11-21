package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.model.ApiResponse
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
import org.springframework.web.bind.annotation.*


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
    fun registerUser(@RequestBody registerUser: RegisterUserDto): ApiResponse<RegisterResponse> {
        return try {
            val response = userService.registerUser(registerUser)
            ApiResponse(true, 200, "Регистрация успешна", response)
        } catch (e: Exception) {
            ApiResponse(false, 400, e.message ?: "Ошибка регистрации")
        }
    }

    @Operation(summary = "Авторизация")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        return try {
            val response = userService.login(request)
            ApiResponse(true, 200, "Авторизация успешна", response)
        } catch (e: Exception) {
            ApiResponse(false, 401, e.message ?: "Ошибка авторизации")
        }
    }

    @Operation(summary = "Обновление JWT токена")
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ApiResponse<RefreshTokenResponse> {
        return try {
            val response = userService.refreshToken(request)
            ApiResponse(true, 200, "Токен обновлен", response)
        } catch (e: Exception) {
            ApiResponse(false, 400, e.message ?: "Ошибка обновления токена")
        }
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