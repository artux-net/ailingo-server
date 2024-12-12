package net.artux.ailingo.server.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.artux.ailingo.server.configuration.RegistrationConfig;
import net.artux.ailingo.server.configuration.security.JwtUtil;
import net.artux.ailingo.server.entity.ChatHistoryEntity;
import net.artux.ailingo.server.entity.SavedTopicsEntity;
import net.artux.ailingo.server.entity.TopicEntity;
import net.artux.ailingo.server.entity.user.Role;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.RegisterUserDto;
import net.artux.ailingo.server.model.Status;
import net.artux.ailingo.server.model.UpdateUserProfileDto;
import net.artux.ailingo.server.model.UserDto;
import net.artux.ailingo.server.model.login.LoginRequest;
import net.artux.ailingo.server.model.login.LoginResponse;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenRequest;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenResponse;
import net.artux.ailingo.server.model.register.RegisterResponse;
import net.artux.ailingo.server.repositories.UserRepository;
import net.artux.ailingo.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RegistrationConfig registrationConfig;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            RegisterUserDto registerUserDto = new RegisterUserDto("admin", "pass", "test@test.com", "admin", "");
            UserEntity userEntity = new UserEntity(registerUserDto, passwordEncoder);
            userEntity.setRole(Role.ADMIN);
            userRepository.save(userEntity);
        }
    }

    @Override
    public RegisterResponse registerUser(RegisterUserDto newUser) {
        if (newUser == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть пустыми.");
        }

        String email = newUser.getEmail().toLowerCase();

        Set<String> allowedEmails = registrationConfig.getAllowedEmails();
        if (!email.endsWith("@artux.net") && !allowedEmails.contains(email)) {
            throw new IllegalArgumentException("Регистрация разрешена только для почт с доменом @artux.net или для конкретных адресов.");
        }

        Status status = userValidator.checkUser(newUser);
        if (!status.isSuccess()) {
            throw new IllegalArgumentException(status.getDescription());
        }

        UserEntity userEntity = saveUser(newUser);
        String jwtToken = jwtUtil.generateToken(userEntity);
        String refreshToken = jwtUtil.generateRefreshToken(userEntity);

        logger.info("Пользователь {} ({}) зарегистрирован.", userEntity.getLogin(), userEntity.getName());

        return new RegisterResponse(jwtToken, refreshToken, dto(userEntity));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        var user = userRepository.findByLogin(request.getLogin()).orElseThrow();

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken, dto(user));
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenResponse response = jwtUtil.refreshToken(request.getRefreshToken());

        if (response == null) {
            throw new IllegalArgumentException("Refresh token is invalid or expired.");
        }
        return response;
    }

    public UserEntity saveUser(RegisterUserDto registerUserDto) {
        UserEntity userEntity = new UserEntity(registerUserDto, passwordEncoder);
        return userRepository.save(userEntity);
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
        return new UserDto(userEntity.getId(), userEntity.getLogin(), userEntity.getName(), userEntity.getEmail(), userEntity.getAvatar(),
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

    public void changeUserStreak() {
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        UserEntity currentUser = getCurrentUser();
        Instant lastStrikeAt = currentUser.getLastSession().truncatedTo(ChronoUnit.DAYS);
        if (lastStrikeAt.isBefore(yesterday)) {
            currentUser.setStreak(0);
        } else {
            if (lastStrikeAt != today) {
                currentUser.setStreak(currentUser.getStreak() + 1);
                currentUser.setLastSession(today);
            }
        }
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

    public Status changeCoinsForCurrentUser(int amount) {
        UserEntity user = getCurrentUser();
        if (amount > 0) {
            user.changeCoins(amount);
            userRepository.save(user);
            return new Status(true, "Монеты зачислены.");
        } else {
            if (user.getCoins() < Math.abs(amount)) {
                return new Status(false, "Недостаточно монет.");
            } else {
                user.changeCoins(amount);
                userRepository.save(user);
                return new Status(true, "Топик получен.");
            }
        }
    }

    @Override
    public Status updateUserProfile(UpdateUserProfileDto updateUserProfile) {
        if (updateUserProfile == null) {
            return new Status(false, "Данные для обновления не переданы.");
        }

        UserEntity currentUser = getCurrentUser();

        boolean isUpdated = false;

        if (!updateUserProfile.getName().equals(currentUser.getName())) {
            Status nameStatus = userValidator.checkName(updateUserProfile.getName());
            if (!nameStatus.isSuccess()) {
                return nameStatus;
            }

            currentUser.setName(updateUserProfile.getName());
            isUpdated = true;
        }

        if (!updateUserProfile.getEmail().equals(currentUser.getEmail())) {
            Status emailStatus = userValidator.checkEmail(updateUserProfile.getEmail());
            if (!emailStatus.isSuccess()) {
                return emailStatus;
            }

            currentUser.setEmail(updateUserProfile.getEmail());
            isUpdated = true;
        }

        if (!updateUserProfile.getAvatar().equals(currentUser.getAvatar())) {
            currentUser.setAvatar(updateUserProfile.getAvatar());
            isUpdated = true;
        }

        if (!isUpdated) {
            return new Status(false, "Нет изменений для обновления.");
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

        if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            return new Status(false, "Новый пароль не должен совпадать с текущим.");
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
