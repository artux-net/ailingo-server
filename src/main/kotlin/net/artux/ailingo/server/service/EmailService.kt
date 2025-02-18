package net.artux.ailingo.server.service

interface EmailService {
    fun sendVerificationEmail(to: String, verificationCode: String)
}