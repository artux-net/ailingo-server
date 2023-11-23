package org.ailingo.server

import org.ailingo.server.model.RegisterUserDto
import org.ailingo.server.model.Status
import org.ailingo.server.service.user.UserRepository
import org.ailingo.server.service.user.UserValidator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserValidatorTests {

    @Autowired
    lateinit var validator: UserValidator

    @Test
    fun testCheckUser_AllValid() {
        val validUser = RegisterUserDto("username",  "password123", "john@example.com","John", "https://webref.ru/example/image/fox.png")
        val result: Status = validator.checkUser(validUser)
        assertTrue(result.isSuccess())
    }

    @Test
    fun testCheckUser_InvalidLogin() {
        val invalidUser = RegisterUserDto("@#$%",  "password123", "john@example.com", "John","https://webref.ru/example/image/fox.png")
        val result: Status = validator.checkUser(invalidUser)
        assertFalse(result.isSuccess())
    }

    @Test
    fun testCheckUser_InvalidPassword() {
        val invalidUser = RegisterUserDto("username",  "pass", "john@example.com", "John","https://webref.ru/example/image/fox.png")
        val result: Status = validator.checkUser(invalidUser)
        assertFalse(result.isSuccess())
    }

    @Test
    fun testCheckUser_InvalidEmail() {
        val invalidUser = RegisterUserDto("username",  "password123", "johnexample.com", "John","https://webref.ru/example/image/fox.png")
        val result: Status = validator.checkUser(invalidUser)
        assertFalse(result.isSuccess())
    }

    @Test
    fun testCheckUser_InvalidName() {
        val invalidUser = RegisterUserDto("username",  "password123", "john@example.com", "John =)","https://webref.ru/example/image/fox.png")
        val result: Status = validator.checkUser(invalidUser)
        assertFalse(result.isSuccess())
    }

    @Test
    fun testCheckUser_InvalidAvatar() {
        val invalidUser = RegisterUserDto("username",  "password123", "john@example.com", "John","https")
        val result: Status = validator.checkUser(invalidUser)
        assertFalse(result.isSuccess())
    }
}