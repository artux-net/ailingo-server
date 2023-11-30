package org.ailingo.server.entity.topic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;
import org.ailingo.server.entity.context.ContextEntity;
import org.ailingo.server.entity.user.UserEntity;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "topic")
public class TopicEntity extends BaseEntity {

    private String title;
    private String imageLink;
    private String contexts;
    private Integer priority;
    private Integer coinCost;
    private String status;

    @ManyToMany
    private Set<UserEntity> users = new HashSet<>();
    @ManyToMany
    private Set<ContextEntity> setContexts = new HashSet<>();
}