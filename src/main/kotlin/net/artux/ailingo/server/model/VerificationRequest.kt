package net.artux.ailingo.server.model

data class VerificationRequest(
    val email: String,
    val verificationCode: String
)