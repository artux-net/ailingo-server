package net.artux.ailingo.server.repositories;

import net.artux.ailingo.server.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> getByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findMemberByEmail(String email);

    Optional<UserEntity> getByEmail(String email);
}
