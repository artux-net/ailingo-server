package net.artux.ailingo.server.util

import net.artux.ailingo.server.dto.RegisterUserDto
import net.artux.ailingo.server.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
class UserValidator(
    private val userRepository: UserRepository
) {

    companion object {
        private const val EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$"
        private const val LOGIN_VALIDATION_REGEX = "^[a-zA-Z0-9-_.]+\$"
        private const val NAME_VALIDATION_REGEX = "^[A-Za-z\u0400-\u052F' ]*\$"
        private const val PASSWORD_VALIDATION_REGEX = "^[A-Za-z\\d!@#$%^&*()_+№\";:?><\\[\\]{}]*\$"
    }

    fun validateUserRegistration(user: RegisterUserDto) {
        validateLogin(user.login)
        validateName(user.name)
        validatePassword(user.password)
        validateEmail(user.email)
    }

    fun validateLogin(login: String?) {
        if (!StringUtils.hasText(login)) {
            throw InvalidRequestException("Логин не может быть пустым.")
        }
        if (!login!![0].isLetter()) {
            throw InvalidRequestException("Логин должен начинаться с буквы.")
        }
        val defectSymbols = checkStringSymbolsByRegexp(login, LOGIN_VALIDATION_REGEX)
        if (defectSymbols.isNotEmpty()) {
            throw InvalidRequestException(
                "Логин содержит запрещённые символы: " + defectSymbols.joinToString(", ")
            )
        }
        if (login.length < 4 || login.length > 16) {
            throw InvalidRequestException("Логин должен содержать не менее 4 и не более 16 символов.")
        }
        if (userRepository.findByLogin(login).isPresent) {
            throw InvalidRequestException("Пользователь с таким логином уже существует.")
        }
    }

    fun validateName(name: String?) {
        if (!StringUtils.hasText(name)) {
            throw InvalidRequestException("Имя не может быть пустым.")
        }
        val defectSymbols = checkStringSymbolsByRegexp(name, NAME_VALIDATION_REGEX)
        if (defectSymbols.isNotEmpty()) {
            throw InvalidRequestException(
                "Имя содержит запрещённые символы: " + defectSymbols.joinToString(", ")
            )
        }
        if (name!!.length < 2 || name.length > 24) {
            throw InvalidRequestException("Имя должно содержать не менее 2 и не более 24 символов.")
        }
    }

    fun validatePassword(password: String?) {
        if (!StringUtils.hasText(password)) {
            throw InvalidRequestException("Пароль не может быть пустым.")
        }
        val defectSymbols = checkStringSymbolsByRegexp(password, PASSWORD_VALIDATION_REGEX)
        if (defectSymbols.isNotEmpty()) {
            throw InvalidRequestException(
                "Пароль содержит запрещённые символы: " + defectSymbols.joinToString(", ")
            )
        }
        if (password!!.length < 8 || password.length > 24) {
            throw InvalidRequestException("Пароль должен содержать не менее 8 и не более 24 символов.")
        }
    }

    fun validateEmail(email: String?) {
        if (!StringUtils.hasText(email)) {
            throw InvalidRequestException("Почта не может быть пустой.")
        }
        if (!email!!.matches(EMAIL_VALIDATION_REGEX.toRegex())) {
            throw InvalidRequestException("Почта имеет неверный формат.")
        }
        if (userRepository.findMemberByEmail(email).isPresent) {
            throw InvalidRequestException("Пользователь с таким e-mail уже существует.")
        }
    }

    private fun checkStringSymbolsByRegexp(str: String?, regexp: String): Collection<String> {
        if (!StringUtils.hasText(str)) {
            return emptyList()
        }
        val result = mutableListOf<String>()
        for (chr in str!!) {
            val chrOfStr = chr.toString()
            if (!chrOfStr.matches(regexp.toRegex())) {
                result.add(chrOfStr)
            }
        }
        return result
    }
}
