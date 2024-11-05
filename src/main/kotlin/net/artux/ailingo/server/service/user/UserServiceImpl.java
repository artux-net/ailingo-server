package org.ailingo.server.service.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ailingo.server.chat_history.ChatHistoryEntity;
import org.ailingo.server.configuration.RegistrationConfig;
import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.ailingo.server.model.Status;
import org.ailingo.server.model.UserDto;
import org.ailingo.server.saved_topics.SavedTopicsEntity;
import org.ailingo.server.service.EmailService;
import org.ailingo.server.service.ValuesService;
import org.ailingo.server.topics.TopicEntity;
import org.ailingo.server.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

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
    private final RegistrationConfig registrationConfig;

    private final Map<String, RegisterUserDto> registerUserMap = new HashMap<>();
    private final Timer timer = new Timer();
    private final RandomString randomString = new RandomString();

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            saveUser(new RegisterUserDto("admin", "pass", "test@test.com", "admin", ""));
        }
    }

    @Override
    public Status registerUser(RegisterUserDto newUser) {
        String email = Optional.ofNullable(newUser.getEmail())
                .map(String::toLowerCase)
                .orElse(null);

        if (email == null) {
            return new Status(false, "Адрес электронной почты не может быть пустым.");
        }

        Set<String> allowedEmails = registrationConfig.getAllowedEmails();

        if (!email.endsWith("@artux.net") && !allowedEmails.contains(email)) {
            return new Status(false, "Регистрация разрешена только для почт с доменом @artux.net или для конкретных адресов.");
        }

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

    public String getUserLogin() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public UserEntity getCurrentUser() {
        UserEntity userEntity = userRepository.findByLogin(getUserLogin()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (userEntity.getLastLoginAt().plusSeconds(300).isBefore(Instant.now())) {
            userEntity.setLastLoginAt(Instant.now());
            return userRepository.save(userEntity);
        } else return userEntity;
    }

    @Override
    public UserDto getUserDto() {
        return dto(getCurrentUser());
    }
    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findMemberByEmail(email);
    }

    @Override
    public UserEntity getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Пользователя не существует"));
    }

    public static UserDto dto(UserEntity userEntity) {
        return new UserDto(userEntity.getId(), userEntity.getLogin(), userEntity.getName(),userEntity.getEmail(), userEntity.getAvatar(),
                userEntity.getXp(), userEntity.getCoins(), userEntity.getStreak(),
                userEntity.getRegistration(), userEntity.getLastLoginAt());
    }

    @Override
    public Set<TopicEntity> getUserSavedTopics() {
        UserEntity currentUser = getCurrentUser();
        Set<TopicEntity> savedTopics = new HashSet<>();
        currentUser.getSavedTopics().forEach(savedTopicsEntity -> savedTopics.addAll(savedTopicsEntity.getSavedTopics()));
        return savedTopics;
    }
    @Override
    public void saveUserTopics(Set<TopicEntity> topics) {
        UserEntity currentUser = getCurrentUser();
        SavedTopicsEntity savedTopicsEntity = new SavedTopicsEntity();
        savedTopicsEntity.setUser(currentUser);
        savedTopicsEntity.setSavedTopics(topics);
        currentUser.getSavedTopics().add(savedTopicsEntity);
        userRepository.save(currentUser);
    }

    @Override
    public void removeUserTopic(TopicEntity topic) {
        UserEntity currentUser = getCurrentUser();
        currentUser.getSavedTopics().removeIf(savedTopicsEntity -> savedTopicsEntity.getSavedTopics().contains(topic));
        userRepository.save(currentUser);
    }

    public Status addWordToFavorites(String word) {
        UserEntity currentUser = getCurrentUser();
        currentUser.getFavoriteWords().add(word);
        userRepository.save(currentUser);
        return new Status(true, "Слово добавлено в избранное.");
    }

    public Status removeWordFromFavorites(String word) {
        UserEntity currentUser = getCurrentUser();
        currentUser.getFavoriteWords().remove(word);
        userRepository.save(currentUser);
        return new Status(true, "Слово удалено из избранного.");
    }

    public Set<String> getFavoriteWords() {
        return getCurrentUser().getFavoriteWords();
    }

    public void addCoinsToCurrentUser(int amount) {
        UserEntity user = getCurrentUser();
        user.addCoins(amount);
        userRepository.save(user);
    }

    public void removeCoinsFromCurrentUser(int amount) {
        UserEntity user = getCurrentUser();
        user.removeCoins(amount);
        userRepository.save(user);
    }

    @Override
    public Status updateUserProfile(String name, String email, String avatar) {
        UserEntity currentUser = getCurrentUser();

        Status nameStatus = userValidator.checkName(name);
        if (!nameStatus.isSuccess()) {
            return nameStatus;
        }
        Status emailStatus = userValidator.checkEmail(email);
        if (!emailStatus.isSuccess()) {
            return emailStatus;
        }
        if (avatar != null && !avatar.isEmpty()) {
            currentUser.setAvatar(avatar);
        }
        userRepository.save(currentUser);
        return new Status(true, "Профиль успешно обновлен.");
    }

    @Override
    public Status changePassword(String oldPassword, String newPassword) {
        UserEntity currentUser = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return new Status(false, "Старый пароль введен неверно.");
        }
        Status status = userValidator.checkPassword(newPassword);
        if (!status.isSuccess()) {
            return status;
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return new Status(true, "Пароль успешно изменен.");
    }

    @Override
    public void saveUserChat(UserEntity user, String chatContent) {
        ChatHistoryEntity chat = new ChatHistoryEntity();
        chat.setUser(user);
        chat.setChatContent(chatContent);
        user.getChatHistory().add(chat);
        userRepository.save(user);
    }

    @Override
    public List<ChatHistoryEntity> getUserChats(UserEntity user) {
        return new ArrayList<>(user.getChatHistory());
    }
}
