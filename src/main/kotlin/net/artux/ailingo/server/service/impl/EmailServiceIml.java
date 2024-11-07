package net.artux.ailingo.server.service.impl;

import lombok.RequiredArgsConstructor;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.RegisterUserDto;
import net.artux.ailingo.server.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EmailServiceIml implements EmailService {

    //private final JavaMailSender mailSender;
    private final ValuesService valuesService;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final String mailTemplateReg;
    private static final String mailTemplateCon;

    static {
        mailTemplateReg = getResource("/mail/mail-template-reg.html");
        mailTemplateCon = getResource("/mail/mail-template-con.html");
    }

    private static String getResource(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "File at path " + path + " is not found.";
        }
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        /*MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom("Сталкерский ПДА <" + valuesService.getEmail() + ">");
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(text, true);
        } catch (MessagingException e) {
            logger.error("Sent error", e);
        }
        mailSender.send(mimeMessage);*/
    }

    public void askForPassword(UserEntity user, String token) {
        sendSimpleMessage(user.getEmail(), "Восстановление пароля", user.getLogin() + ", "
                + "\n\n ваша ссылка для смены пароля: " + valuesService.getAddress() + "/reset/password?t=" + token
                + "\n\n Действует в течение 30 минут.");

    }

    public void sendRegisterLetter(RegisterUserDto user) {
        sendSimpleMessage(user.getEmail(), "Регистрация", mailTemplateReg
                .replace("${login}", Objects.requireNonNull(user.getLogin()))
                .replace("${pass}", Objects.requireNonNull(user.getPassword())));
    }

    public void sendConfirmLetter(RegisterUserDto user, String token) {
        String confirmLink = valuesService.getAddress() + "/confirmation/register?t=" + token;

        if (!valuesService.isEmailConfirmationEnabled())
            return;

        sendSimpleMessage(user.getEmail(), "Подтвердите регистрацию", mailTemplateCon
                .replace("${link}", confirmLink));
    }


}
