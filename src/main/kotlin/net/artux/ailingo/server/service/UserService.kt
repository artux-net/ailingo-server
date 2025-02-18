package net.artux.ailingo.server.service

import net.artux.ailingo.server.jwt.model.RefreshTokenRequest
import net.artux.ailingo.server.jwt.model.RefreshTokenResponse
import net.artux.ailingo.server.dto.RegisterUserDto
import net.artux.ailingo.server.dto.UpdateUserProfileDto
import net.artux.ailingo.server.dto.UserDto
import net.artux.ailingo.server.entity.UserEntity
import net.artux.ailingo.server.model.ChangeCoinsResponse
import net.artux.ailingo.server.model.LoginRequest
import net.artux.ailingo.server.model.LoginResponse
import net.artux.ailingo.server.model.RegisterResponse
import net.artux.ailingo.server.model.ResendVerificationCodeRequest
import net.artux.ailingo.server.model.VerificationRequest

interface UserService {
    fun registerUser(registerUser: RegisterUserDto): RegisterResponse
    fun login(request: LoginRequest): LoginResponse
    fun refreshToken(request: RefreshTokenRequest): RefreshTokenResponse
    fun getCurrentUser(): UserEntity
    fun getUserDto(): UserDto
    fun getUserLogin(): String
    fun changeUserStreak()
    fun changeCoinsForCurrentUser(amount: Int): ChangeCoinsResponse
    fun updateUserProfile(updateUserProfile: UpdateUserProfileDto?): UserDto
    fun verifyEmail(verificationRequest: VerificationRequest): UserDto
    fun resendVerificationCode(resendVerificationCodeRequest: ResendVerificationCodeRequest)
}