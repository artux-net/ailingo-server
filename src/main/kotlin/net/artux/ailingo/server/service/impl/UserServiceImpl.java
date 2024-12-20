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
import net.artux.ailingo.server.model.UserDto;
import net.artux.ailingo.server.model.login.LoginRequest;
import net.artux.ailingo.server.model.login.LoginResponse;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenRequest;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenResponse;
import net.artux.ailingo.server.model.register.RegisterResponse;
import net.artux.ailingo.server.repositories.UserRepository;
import net.artux.ailingo.server.service.EmailService;
import net.artux.ailingo.server.service.UserService;
import net.artux.ailingo.server.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
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
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final Map<String, RegisterUserDto> registerUserMap = new HashMap<>();
    private final Timer timer = new Timer();
    private final RandomString randomString = new RandomString();
    private final RegistrationConfig registrationConfig;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            saveUser(new RegisterUserDto("admin", "pass", "test@test.com", "admin", ""));
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
        userEntity.setRole(Role.USER);
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
