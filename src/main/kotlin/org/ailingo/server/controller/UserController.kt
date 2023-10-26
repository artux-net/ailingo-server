package org.ailingo.server.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.ailingo.server.model.RegisterUserDto
import org.ailingo.server.model.Status
import org.ailingo.server.model.UserDto
import org.ailingo.server.user.UserService
import org.ailingo.server.user.reset.ResetService
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
}