package net.artux.ailingo.server.repository;

import net.artux.ailingo.server.entity.HistoryMessageEntity;
import net.artux.ailingo.server.entity.TopicEntity;
import net.artux.ailingo.server.entity.UserEntity;
import net.artux.ailingo.server.model.ConversationDto;
import net.artux.ailingo.server.model.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageHistoryRepository extends JpaRepository<HistoryMessageEntity, Long> {

    List<HistoryMessageEntity> findByConversationIdAndOwnerOrderByTimestamp(UUID id, UserEntity owner);

    @Query("SELECT h.conversationId as conversationId, " +
            "       t.name as topicName, " +
            "       MIN(h.timestamp) as creationTimestamp " +
            "FROM HistoryMessageEntity h JOIN h.topic t " +
            "WHERE h.owner = :owner " +
            "GROUP BY h.conversationId, t.name " +
            "ORDER BY creationTimestamp DESC")
    List<ConversationDto> findAllByOwner(UserEntity owner);

    Boolean existsByTopicAndOwnerAndType(TopicEntity topic,  UserEntity owner, MessageType type);
}