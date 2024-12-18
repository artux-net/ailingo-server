package net.artux.ailingo.server.service;


import net.artux.ailingo.server.entity.ChatHistoryEntity;
import net.artux.ailingo.server.entity.TopicEntity;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.RegisterUserDto;
import net.artux.ailingo.server.model.Status;
import net.artux.ailingo.server.model.UserDto;
import net.artux.ailingo.server.model.login.LoginRequest;
import net.artux.ailingo.server.model.login.LoginResponse;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenRequest;
import net.artux.ailingo.server.model.refreshtoken.RefreshTokenResponse;
import net.artux.ailingo.server.model.register.RegisterResponse;

import java.util.*;

public interface UserService {

    RegisterResponse registerUser(RegisterUserDto registerUser);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    UserEntity getCurrentUser();

    UserDto getUserDto();

    String getUserLogin();

    Optional<UserEntity> getUserByEmail(String email);

    UserEntity getUserByLogin(String login);

    List<UserEntity> getUsersByIds(Collection<UUID> ids);

    Set<TopicEntity> getUserSavedTopics();

    void saveUserTopics(Set<TopicEntity> topics);

    void removeUserTopic(TopicEntity topic);

    Status addWordToFavorites(String word);

    Status removeWordFromFavorites(String word);

    Set<String> getFavoriteWords();

    void addCoinsToCurrentUser(int amount);

    void removeCoinsFromCurrentUser(int amount);

    Status updateUserProfile(String name, String email, String avatar);

    Status changePassword(String oldPassword, String newPassword);


    public void saveUserChat(UserEntity user, String chatContent);

    public List<ChatHistoryEntity> getUserChats(UserEntity user);
}
