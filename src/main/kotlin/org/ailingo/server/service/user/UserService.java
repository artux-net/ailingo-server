package org.ailingo.server.service.user;


import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.ailingo.server.model.Status;
import org.ailingo.server.model.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Status registerUser(RegisterUserDto registerUser);

    Status handleConfirmation(String token);

    UserEntity getCurrentUser();

    UserDto getUserDto();

    UserEntity getCurrentUser(UUID objectId);

    String getUserLogin();

    Optional<UserEntity> getUserByEmail(String email);

    UserEntity getUserByLogin(String login);

    void deleteUserById(UUID id);

    boolean changeEmailSetting(UUID id);

    List<UserEntity> getUsersByIds(Collection<UUID> ids);
    List<UserEntity> getUsersByLogins(Collection<String> logins);

}
