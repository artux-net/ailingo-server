package org.ailingo.server.saved_topics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.topics.TopicEntity;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "saved_topics")
@Getter
@Setter
public class SavedTopicsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_saved_topics",
            joinColumns = @JoinColumn(name = "saved_topics_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<TopicEntity> savedTopics = new HashSet<>();
}