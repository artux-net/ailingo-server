package org.ailingo.server.service.user;


import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.ailingo.server.model.Status;
import org.ailingo.server.model.UserDto;
import org.ailingo.server.topics.TopicEntity;

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
}
