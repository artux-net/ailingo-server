package net.artux.ailingo.server.repositories;

import net.artux.ailingo.server.entity.HistoryMessageEntity;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.ConversationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageHistoryRepository extends JpaRepository<HistoryMessageEntity, Long> {

    Optional<HistoryMessageEntity> findByOwner(UserEntity owner);

    List<HistoryMessageEntity> findByConversationIdAndOwnerOrderByTimestamp(UUID id, UserEntity owner);

    List<ConversationDto> findAllByOwner(UserEntity owner);

}