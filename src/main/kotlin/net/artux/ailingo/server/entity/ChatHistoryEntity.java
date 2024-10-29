package net.artux.ailingo.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.artux.ailingo.server.entity.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "chat_history")
public class ChatHistoryEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String chatContent;

    private LocalDateTime timestamp;

}