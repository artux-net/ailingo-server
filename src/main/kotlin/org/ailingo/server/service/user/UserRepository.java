package org.ailingo.server.service.user;

import org.ailingo.server.entity.user.SimpleUser;
import org.ailingo.server.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Transactional
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    List<UserEntity> findAllByReceiveEmailsTrue();

    Optional<UserEntity> getByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findMemberByEmail(String email);

    Set<UserEntity> findAllByIdIn(List<UUID> list);

    List<UserEntity> findAllByLoginIn(Collection<String> logins);

    Page<UserEntity> findByLoginContainingIgnoreCase(String title, Pageable pageable);

    int countAllByLastLoginAtAfter(Instant afterTime);

    int countAllByRegistrationAfter(Instant afterTime);

    Optional<UserEntity> getByEmail(String email);
}
