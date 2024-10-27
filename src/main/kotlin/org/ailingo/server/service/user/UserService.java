package org.ailingo.server.service.user;


import org.ailingo.server.entity.ChatHistoryEntity;
import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.ailingo.server.model.Status;
import org.ailingo.server.model.UserDto;
import org.ailingo.server.entity.TopicEntity;

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

    void addCoinsToCurrentUser(int amount);

    void removeCoinsFromCurrentUser(int amount);

    Status updateUserProfile(String name, String email, String avatar);

    Status changePassword(String oldPassword, String newPassword);


    public void saveUserChat(UserEntity user, String chatContent);

    public List<ChatHistoryEntity> getUserChats(UserEntity user);
}
