package net.artux.ailingo.server.service;


import net.artux.ailingo.server.entity.ChatHistoryEntity;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.RegisterUserDto;
import net.artux.ailingo.server.model.Status;
import net.artux.ailingo.server.model.UserDto;
import net.artux.ailingo.server.entity.TopicEntity;

import java.util.*;

public interface UserService {

    Status registerUser(RegisterUserDto registerUser);

    Status handleConfirmation(String token);

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

    Status changeCoinsForCurrentUser(int amount);

    Status updateUserProfile(String name, String email, String avatar);

    Status changePassword(String oldPassword, String newPassword);


    public void saveUserChat(UserEntity user, String chatContent);

    public List<ChatHistoryEntity> getUserChats(UserEntity user);
}
