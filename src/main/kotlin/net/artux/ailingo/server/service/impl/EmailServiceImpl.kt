package net.artux.ailingo.server.service.impl

import lombok.RequiredArgsConstructor
import net.artux.ailingo.server.service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val mailUsername: String
) : EmailService {

    override fun sendVerificationEmail(to: String, verificationCode: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = "Подтверждение регистрации на AiLingo"
        message.text = """
                Здравствуйте!
    
                Благодарим вас за регистрацию в AiLingo.
                Для подтверждения вашего email, пожалуйста, введите следующий код подтверждения:
    
                $verificationCode
    
                Если возникли проблемы с регистрацией, напишите на почту vangelnum@gmail.com
                        
        """.trimIndent()
        message.from = mailUsername

        mailSender.send(message)
    }
}
