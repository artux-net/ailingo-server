package net.artux.ailingo.server.service.impl

import jakarta.annotation.PostConstruct
import lombok.RequiredArgsConstructor
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
import net.artux.ailingo.server.model.ResendVerificationCodeRequest
import net.artux.ailingo.server.model.Role
import net.artux.ailingo.server.model.StreakCheckResult
import net.artux.ailingo.server.model.VerificationRequest
import net.artux.ailingo.server.repository.PendingUserRepository
import net.artux.ailingo.server.repository.UserRepository
import net.artux.ailingo.server.service.EmailService
import net.artux.ailingo.server.service.UserService
import net.artux.ailingo.server.util.GlobalExceptionHandler
import net.artux.ailingo.server.util.InvalidRequestException
import net.artux.ailingo.server.util.UserValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID
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
            val registerUserDto = RegisterUserDto("admin", "password", "test@test.net", "admin")
            val adminEntity = UserEntity(
                null,
                registerUserDto.login,
                registerUserDto.email,
                passwordEncoder.encode(registerUserDto.password),
                registerUserDto.name,
                null,
            )
            adminEntity.role = Role.ADMIN
            adminEntity.isEmailVerified = true
            userRepository.save(adminEntity)
            logger.info("Default admin user created successfully.")

            val simpleUser = RegisterUserDto("user", "password", "user@user.net", "userName")
            val simpleUserEntity = UserEntity(
                null,
                simpleUser.login,
                simpleUser.email,
                passwordEncoder.encode(simpleUser.password),
                simpleUser.name,
                null,
            )
            simpleUserEntity.isEmailVerified = true
            userRepository.save(simpleUserEntity)
            logger.info("Default user created successfully.")
        } else {
            logger.info("Users already exist, skipping default admin user creation.")
        }
    }

    override fun registerUser(registerUser: RegisterUserDto) {
        val email = registerUser.email.lowercase(Locale.getDefault())

        userValidator.validateUserRegistration(registerUser)

        if (userRepository.findByLogin(registerUser.login).isPresent) {
            throw InvalidRequestException(GlobalExceptionHandler.USER_ALREADY_EXISTS_MESSAGE)
        }
        val existingUser = userRepository.findMemberByEmail(email)
        if (existingUser.isPresent) {
            throw InvalidRequestException(GlobalExceptionHandler.USER_ALREADY_EXISTS_MESSAGE)
        }

        val existingPendingUser = pendingUserRepository.findByEmail(email)

        if (existingPendingUser.isPresent) {
            pendingUserRepository.delete(existingPendingUser.get())
            logger.info("Удалена старая pending запись для email: {}", email)
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
    }

    override fun login(request: LoginRequest): LoginResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.login, request.password)
            )
        } catch (e: BadCredentialsException) {
            throw InvalidRequestException("Неверный логин или пароль.")
        } catch (e: Exception) {
            logger.error("Ошибка при аутентификации пользователя {}: {}", request.login, e.message, e)
            throw InvalidRequestException("Ошибка при входе в систему. Попробуйте позже.")
        }
        val user = if (request.login.contains("@")) {
            userRepository.findByEmail(request.login)
        } else {
            userRepository.findByLogin(request.login)
        }.orElseThrow()

        val streakCheckResult = checkAndResetStreakIfNeeded(user)
        user.lastLoginAt = Instant.now()
        userRepository.save(user)

        val accessToken = jwtUtil.generateToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)

        return LoginResponse(
            token = accessToken,
            refreshToken = refreshToken,
            user = getDto(user),
            isStreakContinued = streakCheckResult.streakWasValid
        )
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
        return getDto(getCurrentUser())
    }

    companion object {
        fun getDto(userEntity: UserEntity): UserDto {
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
                userEntity.isEmailVerified,
                userEntity.role,
                userEntity.lastStreakAt
            )
        }
    }

    override fun changeUserStreak() {
        val currentUser = getCurrentUser()
        val now = Instant.now()
        val today = now.truncatedTo(ChronoUnit.DAYS)
        val yesterday = today.minus(1, ChronoUnit.DAYS)

        val lastStreakDay = currentUser.lastStreakAt?.truncatedTo(ChronoUnit.DAYS)

        if (lastStreakDay == today) {
            logger.info("User ${currentUser.login} already had a streak activity today (${currentUser.lastStreakAt}). Streak remains ${currentUser.streak}.")
            currentUser.lastSession = now
            userRepository.save(currentUser)
            return
        }

        var streakUpdated = false
        if (lastStreakDay == yesterday) {
            currentUser.streak += 1
            logger.info("User ${currentUser.login} continued streak from yesterday (${currentUser.lastStreakAt}). New streak: ${currentUser.streak}.")
            streakUpdated = true
        }

        else if (lastStreakDay == null || lastStreakDay.isBefore(yesterday)) {
            if (lastStreakDay != null) {
                logger.info("User ${currentUser.login} streak reset. Last streak activity was on $lastStreakDay, which is before yesterday ($yesterday). Starting new streak.")
            } else {
                logger.info("User ${currentUser.login} starting first streak.")
            }
            currentUser.streak = 1
            streakUpdated = true
        }
        if (streakUpdated) {
            currentUser.lastStreakAt = now
            currentUser.lastSession = now
            userRepository.save(currentUser)
            logger.info("User ${currentUser.login} streak updated. LastStreakAt: ${currentUser.lastStreakAt}, LastSession: ${currentUser.lastSession}")
        } else {
            logger.warn("User ${currentUser.login}: changeUserStreak called, but no condition met to update streak. lastStreakAt=${currentUser.lastStreakAt}, today=$today")
            currentUser.lastSession = now
            userRepository.save(currentUser)
        }
    }

    override fun changeCoinsForCurrentUser(amount: Int): ChangeCoinsResponse {
        val user = getCurrentUser()
        val response = ChangeCoinsResponse()

        if (amount > 0) {
            user.changeCoins(amount)
            response.success = true
            response.message = "Успешно зачислено $amount монет."
            response.newBalance = user.coins
        } else if (user.coins < abs(amount)) {
            response.success = false
            response.message = "Недостаточно монет."
            response.newBalance = user.coins
        } else {
            user.changeCoins(amount)
            response.success = true
            response.message = "Успешно списано ${Math.abs(amount)} монет."
            response.newBalance = user.coins
        }

        if (response.success) {
            userRepository.save(user)
        }

        return response
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
                                updateUserProfile.avatar != null && updateUserProfile.avatar != currentUser.avatar
                        )
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
        return getDto(currentUser)
    }

    override fun verifyEmail(verificationRequest: VerificationRequest): LoginResponse {
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

        userRepository.save(userEntity)

        val accessToken = jwtUtil.generateToken(userEntity)
        val refreshToken = jwtUtil.generateRefreshToken(userEntity)
        return LoginResponse(
            token = accessToken,
            refreshToken = refreshToken,
            user = getDto(userEntity),
            isStreakContinued = false
        )
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

    override fun getUserByLogin(login: String): UserEntity {
        return userRepository.findByLogin(login).orElseThrow { RuntimeException("Пользователя не существует") }
    }

    override fun getUsersByIds(ids: Collection<UUID>): List<UserEntity> {
        return userRepository.findAllById(ids)
    }

    fun getDto(userEntity: UserEntity) = UserDto(
        userEntity.id,
        userEntity.login,
        userEntity.name,
        userEntity.email,
        userEntity.avatar,
        userEntity.xp,
        userEntity.coins,
        userEntity.streak,
        userEntity.registration,
        userEntity.lastLoginAt,
        userEntity.isEmailVerified,
        userEntity.role,
        userEntity.lastStreakAt
    )

    private fun generateVerificationCode(): String {
        val random = SecureRandom()
        return String.format("%06d", random.nextInt(1000000))
    }

    private fun checkAndResetStreakIfNeeded(user: UserEntity): StreakCheckResult {
        val now = Instant.now()
        val today = now.truncatedTo(ChronoUnit.DAYS)
        val yesterday = today.minus(1, ChronoUnit.DAYS)

        val lastStreakDay = user.lastStreakAt?.truncatedTo(ChronoUnit.DAYS)

        var needsSave = false
        var streakWasValid: Boolean

        if (user.streak > 0 && user.lastStreakAt != null) {
            if (lastStreakDay == today || lastStreakDay == yesterday) {
                streakWasValid = true
            } else {
                logger.info("Streak for user ${user.login} expired. Last activity was on $lastStreakDay (${user.lastStreakAt}). Resetting streak from ${user.streak} to 0.")
                user.streak = 0
                user.lastStreakAt = null
                needsSave = true
                streakWasValid = false
            }
        } else {
            if (user.streak > 0 && user.lastStreakAt == null) {
                logger.warn("User ${user.login} has streak ${user.streak} but null lastStreakAt. Resetting streak to 0.")
                user.streak = 0
                needsSave = true
            }
            streakWasValid = false
        }

        return StreakCheckResult(streakWasValid = streakWasValid, needsSave = needsSave)
    }
}
