package org.ailingo.server.service.user;

import org.ailingo.server.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> getByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findMemberByEmail(String email);

    Optional<UserEntity> getByEmail(String email);
}
