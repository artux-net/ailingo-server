package net.artux.ailingo.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "topic")
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String name;
    private String image;
    private Integer price;
    private Integer level;

    @Column(name = "welcome_prompt")
    private String welcomePrompt;

    @Lob
    @Column(name = "system_prompt")
    private String systemPrompt;

    @Column(name = "message_limit")
    private Integer messageLimit;
}