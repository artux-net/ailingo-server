package net.artux.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.artux.ailingo.server.dto.RegisterUserDto
import net.artux.ailingo.server.dto.UpdateUserProfileDto
import net.artux.ailingo.server.dto.UserDto
import net.artux.ailingo.server.jwt.model.RefreshTokenRequest
import net.artux.ailingo.server.jwt.model.RefreshTokenResponse
import net.artux.ailingo.server.model.ChangeCoinsResponse
import net.artux.ailingo.server.model.LoginRequest
import net.artux.ailingo.server.model.LoginResponse
import net.artux.ailingo.server.model.ResendVerificationCodeRequest
import net.artux.ailingo.server.model.VerificationRequest
import net.artux.ailingo.server.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Пользователь")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {
    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUser: RegisterUserDto): ResponseEntity<Void> {
        userService.registerUser(registerUser)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "Авторизация")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = userService.login(request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Обновление JWT токена")
    @PostMapping("/refreshToken")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val response = userService.refreshToken(request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Основная информация о текущем пользователе")
    @GetMapping("/info")
    fun getCurrentUserInfo(): ResponseEntity<UserDto> {
        val userDto = userService.getUserDto()
        return ResponseEntity.ok(userDto)
    }

    @Operation(summary = "Изменение количества монет у текущего пользователя")
    @PostMapping("/changeCoins")
    fun changeCoinsForCurrentUser(@RequestParam("amount") amount: Int): ResponseEntity<ChangeCoinsResponse> {
        val response = userService.changeCoinsForCurrentUser(amount)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Обновление профиля пользователя")
    @PostMapping("/updateProfile")
    fun updateUserProfile(@RequestBody updateUserProfile: UpdateUserProfileDto?): ResponseEntity<UserDto> {
        val updatedUserDto = userService.updateUserProfile(updateUserProfile)
        return ResponseEntity.ok(updatedUserDto)
    }

    @Operation(summary = "Верификация email пользователя")
    @PostMapping("/verifyEmail")
    fun verifyEmail(@RequestBody verificationRequest: VerificationRequest): ResponseEntity<LoginResponse> {
        val user = userService.verifyEmail(verificationRequest)
        return ResponseEntity.ok(user)
    }

    @Operation(summary = "Переотправка кода верификации email")
    @PostMapping("/resendVerificationCode")
    fun resendVerificationCode(
        @RequestBody resendVerificationCodeRequest: ResendVerificationCodeRequest
    ): ResponseEntity<String> {
        userService.resendVerificationCode(resendVerificationCodeRequest)
        return ResponseEntity.ok("Новый код верификации отправлен на ваш email")
    }
}
