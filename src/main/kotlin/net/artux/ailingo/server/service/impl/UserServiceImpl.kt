package net.artux.ailingo.server.service.impl

import jakarta.annotation.PostConstruct
import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.core.util.GlobalExceptionHandler
import net.artux.ailingo.server.core.util.InvalidRequestException
import net.artux.ailingo.server.core.util.UserValidator
import net.artux.ailingo.server.core.util.generateVerificationCode
import net.artux.ailingo.server.dto.RegisterUserDto
import net.artux.ailingo.server.dto.UpdateUserProfileDto
import net.artux.ailingo.server.dto.UserDto
import net.artux.ailingo.server.entity.PendingUserEntity
import net.artux.ailingo.server.entity.UserEntity
import net.artux.ailingo.server.jwt.model.RefreshTokenRequest
import net.artux.ailingo.server.jwt.model.RefreshTokenResponse
import net.artux.ailingo.server.jwt.util.JwtUtil
import net.artux.ailingo.server.model.ChangeCoinsResponse
import net.artux.ailingo.server.model.LoginRequest
import net.artux.ailingo.server.model.LoginResponse
import net.artux.ailingo.server.model.RegisterResponse
import net.artux.ailingo.server.model.ResendVerificationCodeRequest
import net.artux.ailingo.server.model.Role
import net.artux.ailingo.server.model.VerificationRequest
import net.artux.ailingo.server.repository.PendingUserRepository
import net.artux.ailingo.server.repository.UserRepository
import net.artux.ailingo.server.service.EmailService
import net.artux.ailingo.server.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

@Service
@Transactional
@RequiredArgsConstructor
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userValidator: UserValidator,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val emailService: EmailService,
    private val authenticationManager: AuthenticationManager,
    private val pendingUserRepository: PendingUserRepository
) : UserService {
    private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @PostConstruct
    fun initAdminUser() {
        if (userRepository.count() == 0L) {
            val registerUserDto = RegisterUserDto("admin", "admin", "test@test.net", "admin")
            val userEntity = UserEntity(
                null,
                registerUserDto.login,
                registerUserDto.email,
                passwordEncoder.encode(registerUserDto.password),
                registerUserDto.name,
                null
            )
            userEntity.role = Role.ADMIN
            userEntity.isEmailVerified = true
            userRepository.save(userEntity)
            logger.info("Default admin user created successfully.")
        } else {
            logger.info("Users already exist, skipping default admin user creation.")
        }
    }

    override fun registerUser(registerUser: RegisterUserDto): RegisterResponse {
        val email = registerUser.email.lowercase(Locale.getDefault())

        userValidator.validateUserRegistration(registerUser)

        if (userRepository.findByLogin(registerUser.login).isPresent) {
            throw InvalidRequestException(GlobalExceptionHandler.USER_ALREADY_EXISTS_MESSAGE + " Login already exists.")
        }
        if (userRepository.findMemberByEmail(email).isPresent || pendingUserRepository.findByEmail(email).isPresent) { // Проверяем и временных пользователей
            throw InvalidRequestException(GlobalExceptionHandler.USER_ALREADY_EXISTS_MESSAGE + " Email already exists.")
        }

        val verificationCode = generateVerificationCode()
        val password = passwordEncoder.encode(registerUser.password)

        val pendingUserEntity = PendingUserEntity(
            login = registerUser.login,
            name = registerUser.name,
            email = email,
            password = password,
            verificationCode = verificationCode
        )

        pendingUserRepository.save(pendingUserEntity)

        try {
            emailService.sendVerificationEmail(email, verificationCode)
        } catch (e: Exception) {
            pendingUserRepository.delete(pendingUserEntity)
            logger.error("Ошибка при отправке email верификации для пользователя {}: {}", email, e.message)
            throw InvalidRequestException(GlobalExceptionHandler.EMAIL_SENDING_FAILED_MESSAGE)
        }

        return RegisterResponse("", "", null)
    }

    override fun login(request: LoginRequest): LoginResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.login, request.password)
            )
        } catch (e: Exception) {
            throw InvalidRequestException("Неверный логин или пароль.")
        }
        val user = userRepository.findByLogin(request.login).orElseThrow()

        val accessToken = jwtUtil.generateToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)

        return LoginResponse(accessToken, refreshToken, dto(user))
    }

    override fun refreshToken(request: RefreshTokenRequest): RefreshTokenResponse {
        return jwtUtil.refreshToken(request.refreshToken)
            ?: throw InvalidRequestException("Токен обновления неверный или истек")
    }

    override fun getUserLogin(): String {
        return SecurityContextHolder.getContext().authentication.name
    }

    override fun getCurrentUser(): UserEntity {
        val login = getUserLogin()
        return userRepository.findByLogin(login)
            .orElseThrow { IllegalStateException("Пользователь с логином $login не найден") }

    }

    override fun getUserDto(): UserDto {
        return dto(getCurrentUser())
    }

    companion object {
        fun dto(userEntity: UserEntity): UserDto {
            return UserDto(
                userEntity.id,
                userEntity.login ?: "",
                userEntity.name ?: "",
                userEntity.email ?: "",
                userEntity.avatar,
                userEntity.xp,
                userEntity.coins,
                userEntity.streak,
                userEntity.registration,
                userEntity.lastLoginAt,
                userEntity.isEmailVerified
            )
        }
    }

    override fun changeUserStreak() {
        val yesterday = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val currentUser = getCurrentUser()
        val lastStrikeAt =
            currentUser.lastSession?.truncatedTo(ChronoUnit.DAYS) ?: Instant.EPOCH // Handle null lastSession

        if (lastStrikeAt.isBefore(yesterday)) {
            currentUser.streak = 0
        } else {
            if (lastStrikeAt != today) {
                currentUser.streak += 1
                currentUser.lastSession = today
            }
        }
        userRepository.save(currentUser)
    }


    override fun changeCoinsForCurrentUser(amount: Int): ChangeCoinsResponse {
        val user = getCurrentUser()
        val response = ChangeCoinsResponse()

        if (amount > 0) {
            user.changeCoins(amount)
            userRepository.save(user)
            response.success = true
            response.message = "Успешно зачислено $amount монет."
            response.newBalance = user.coins
            return response
        } else {
            if (user.coins < abs(amount)) {
                response.success = false
                response.message = "Недостаточно монет."
                response.newBalance = user.coins
                return response
            } else {
                user.changeCoins(amount)
                userRepository.save(user)
                response.success = true
                response.message = "Успешно списано ${Math.abs(amount)} монет."
                response.newBalance = user.coins
                return response
            }
        }
    }

    override fun updateUserProfile(updateUserProfile: UpdateUserProfileDto?): UserDto {
        updateUserProfile ?: throw InvalidRequestException("Данные для обновления не переданы.")

        val currentUser = getCurrentUser()
        var isUpdated = false

        if (!updateUserProfile.newPassword.isNullOrBlank()) {
            if (updateUserProfile.oldPassword.isNullOrBlank()) {
                throw InvalidRequestException("Для изменения пароля необходимо указать старый пароль.")
            }
            try {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        currentUser.login,
                        updateUserProfile.oldPassword
                    )
                )
            } catch (e: Exception) {
                throw InvalidRequestException("Неверный старый пароль.")
            }
            userValidator.validatePassword(updateUserProfile.newPassword)
            currentUser.password = passwordEncoder.encode(updateUserProfile.newPassword)
            isUpdated = true
        }

        if (updateUserProfile.name != null && updateUserProfile.name != currentUser.name ||
            updateUserProfile.email != null && updateUserProfile.email != currentUser.email ||
            updateUserProfile.avatar != null && updateUserProfile.avatar != currentUser.avatar
        ) {

            if (updateUserProfile.oldPassword.isNullOrBlank() && (
                        updateUserProfile.name != null && updateUserProfile.name != currentUser.name ||
                                updateUserProfile.email != null && updateUserProfile.email != currentUser.email ||
                                updateUserProfile.avatar != null && updateUserProfile.avatar != currentUser.avatar)
            ) {
                throw InvalidRequestException("Для изменения данных профиля необходимо указать текущий пароль.")
            }

            if (!updateUserProfile.oldPassword.isNullOrBlank()) {
                try {
                    authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken(currentUser.login, updateUserProfile.oldPassword)
                    )
                } catch (e: Exception) {
                    throw InvalidRequestException("Неверный старый пароль.")
                }
            }

            if (updateUserProfile.name != null && updateUserProfile.name != currentUser.name) {
                userValidator.validateName(updateUserProfile.name)
                currentUser.name = updateUserProfile.name
                isUpdated = true
            }

            if (updateUserProfile.email != null && updateUserProfile.email != currentUser.email) {
                userValidator.validateEmail(updateUserProfile.email)
                currentUser.email = updateUserProfile.email
                isUpdated = true
            }

            if (updateUserProfile.avatar != null && updateUserProfile.avatar != currentUser.avatar) {
                currentUser.avatar = updateUserProfile.avatar
                isUpdated = true
            }
        }

        if (!isUpdated) {
            logger.warn("Нет изменений для обновления профиля пользователя {}.", currentUser.login)
            throw InvalidRequestException("Нет изменений для обновления.")
        }

        userRepository.save(currentUser)
        logger.info("Профиль пользователя {} успешно обновлен.", currentUser.login)
        return dto(currentUser)
    }

    override fun verifyEmail(verificationRequest: VerificationRequest): UserDto {
        val email = verificationRequest.email.lowercase(Locale.getDefault())
        val verificationCode = verificationRequest.verificationCode

        val pendingUser = pendingUserRepository.findByVerificationCode(verificationCode)
            .orElseThrow { InvalidRequestException(GlobalExceptionHandler.INVALID_VERIFICATION_CODE_MESSAGE) }

        if (pendingUser.email != email) {
            throw InvalidRequestException(GlobalExceptionHandler.INVALID_VERIFICATION_CODE_MESSAGE)
        }
        val userEntity = UserEntity(
            null,
            pendingUser.login,
            pendingUser.email,
            pendingUser.password,
            pendingUser.name,
            null
        )
        userEntity.registration = Instant.now()
        userEntity.isEmailVerified = true
        userRepository.save(userEntity)

        pendingUserRepository.delete(pendingUser)

        return dto(userEntity)
    }

    override fun resendVerificationCode(resendVerificationCodeRequest: ResendVerificationCodeRequest) {
        val email = resendVerificationCodeRequest.email.lowercase(Locale.getDefault())

        val user = userRepository.findMemberByEmail(email)
            .orElseThrow { InvalidRequestException(GlobalExceptionHandler.USER_NOT_FOUND) }

        if (user.isEmailVerified) {
            throw InvalidRequestException(GlobalExceptionHandler.EMAIL_ALREADY_VERIFIED_MESSAGE)
        }

        val newVerificationCode = generateVerificationCode()
        user.verificationCode = newVerificationCode
        userRepository.save(user)

        try {
            emailService.sendVerificationEmail(email, newVerificationCode)
        } catch (e: Exception) {
            logger.error("Ошибка при повторной отправке email верификации для пользователя {}: {}", email, e.message)
            throw InvalidRequestException(GlobalExceptionHandler.EMAIL_SENDING_FAILED_MESSAGE)
        }
    }
}