package net.artux.ailingo.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "message_history")
public class HistoryMessageEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "conversation_owner", nullable = false)
    private UserEntity owner;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicEntity topic;

    @Column(nullable = false)
    private UUID conversationId;

    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;
}