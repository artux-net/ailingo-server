package net.artux.ailingo.server.core.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {

    companion object {
        const val INVALID_REQUEST_MESSAGE = "Некорректный запрос."
        const val UNKNOWN_ERROR_MESSAGE = "Произошла неизвестная ошибка."
        const val USER_NOT_FOUND = "Пользователь не найден."
        const val ILLEGAL_ARGUMENT_MESSAGE = "Некорректный аргумент."
        const val ACCESS_DENIED = "Доступ запрещен"
        const val BAD_CREDENTIALS = "Неверные учетные данные пользователя"
        const val EMAIL_ALREADY_VERIFIED_MESSAGE = "Email уже подтвержден"
        const val INVALID_VERIFICATION_CODE_MESSAGE = "Неверный код подтверждения"
        const val USER_ALREADY_EXISTS_MESSAGE = "Пользователь уже существует"
        const val EMAIL_SENDING_FAILED_MESSAGE = "Проблема отправки сообщения на почту. Попробуйте использовать другую почту."
    }

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(
        ex: InvalidRequestException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: INVALID_REQUEST_MESSAGE,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        ex: IllegalStateException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: USER_NOT_FOUND,
            status = HttpStatus.NOT_FOUND.value()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: ILLEGAL_ARGUMENT_MESSAGE,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: BAD_CREDENTIALS,
            HttpStatus.UNAUTHORIZED.value()
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ACCESS_DENIED,
            status = HttpStatus.FORBIDDEN.value()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = UNKNOWN_ERROR_MESSAGE,
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

class InvalidRequestException(message: String) : RuntimeException(message)