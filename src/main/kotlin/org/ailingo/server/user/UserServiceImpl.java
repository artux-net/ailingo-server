package org.ailingo.server.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.ailingo.server.model.SecurityUser;
import org.ailingo.server.model.Status;
import org.ailingo.server.model.UserDto;
import org.ailingo.server.model.UserMapper;
import org.ailingo.server.service.EmailService;
import org.ailingo.server.service.ValuesService;
import org.ailingo.server.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ValuesService valuesService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, RegisterUserDto> registerUserMap = new HashMap<>();
    private final Timer timer = new Timer();
    private final Environment environment;
    private final UserMapper mapper;
    private final RandomString randomString = new RandomString();

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            saveUser(new RegisterUserDto("admin", "pass", "test@test.com", "admin", ""));
        }
    }

    @Override
    public Status registerUser(RegisterUserDto newUser) {
        Status status = userValidator.checkUser(newUser);
        if (!status.isSuccess())
            return status;

        if (registerUserMap.containsValue(newUser))
            return new Status(false, "Пользователь ожидает регистрации, проверьте почту.");

        try {
            String token = generateToken(newUser);
            if (valuesService.isEmailConfirmationEnabled()) {
                emailService.sendConfirmLetter(newUser, token);
                return new Status(true, "Проверьте почту.");
            } else {
                handleConfirmation(token);
                return new Status(true, "Учетная запись зарегистрирована. Выполните вход.");
            }

        } catch (Exception e) {
            logger.error("Registration", e);
            return new Status(false, "Не удалось отправить письмо на " + newUser.getEmail());
        }
    }

    private String generateToken(RegisterUserDto user) {
        String token = randomString.nextString();
        logger.info("Пользователь {} добавлен в лист ожидания регистрации с токеном {}, токен возможно использовать через сваггер.", user.getEmail(), token);
        logger.info("Ссылка подтверждения аккаунта: " + valuesService.getAddress() + "/confirmation/register?t=" + token);
        registerUserMap.put(token, user);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                registerUserMap.remove(token);
            }
        }, 10 * 60 * 1000);
        return token;
    }

    public Status handleConfirmation(String token) {
        if (registerUserMap.containsKey(token)) {
            RegisterUserDto regDto = registerUserMap.get(token);
            Status currentStatus = userValidator.checkUser(regDto);
            registerUserMap.remove(token);
            if (!currentStatus.isSuccess())
                return currentStatus;

            UserEntity member = saveUser(regDto);
            logger.info("Пользователь {} ({}) зарегистрирован.", member.getLogin(), member.getName());
            try {
                if (valuesService.isEmailConfirmationEnabled())
                    emailService.sendRegisterLetter(regDto);
                return new Status(true, "Мы вас зарегистрировали, спасибо!");
            } catch (Exception e) {
                logger.error("Handle confirmation", e);
                return new Status(true, "Не получилось отправить подтверждение на почту, но мы вас зарегистрировали, спасибо!");
            }
        } else return new Status(false, "Ссылка устарела или не существует");
    }

    public UserEntity saveUser(RegisterUserDto registerUserDto) {
        return userRepository.save(new UserEntity(registerUserDto, passwordEncoder));
    }

    @Override
    public List<UserEntity> getUsersByIds(Collection<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public UUID getCurrentId() {
        return ((SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();
    }

    @Override
    public UserEntity getUserById() {
        UserEntity userEntity = userRepository.findById(getCurrentId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (userEntity.getLastLoginAt().plusSeconds(300).isBefore(Instant.now())) {
            userEntity.setLastLoginAt(Instant.now());
            return userRepository.save(userEntity);
        } else return userEntity;
    }

    @Override
    public UserDto getUserDto() {
        return mapper.dto(getUserById());
    }

    @Override
    public UserEntity getUserById(UUID objectId) {
        return userRepository.findById(objectId).orElseThrow();
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findMemberByEmail(email);
    }

    @Override
    public UserEntity getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Пользователя не существует"));
    }

    @Override
    public List<UserEntity> getUsersByLogins(Collection<String> logins) {
        return userRepository.findAllByLoginIn(logins);
    }

    @Override
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean changeEmailSetting(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow();
        user.setReceiveEmails(!user.getReceiveEmails());
        return userRepository.save(user)
                .getReceiveEmails();
    }
}
