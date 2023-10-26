package org.ailingo.server.model;

import org.ailingo.server.entity.user.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterUserDto regUser(UserEntity user);

    UserDto dto(UserEntity user);

}
