package org.ailingo.server.entity.context;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;
import org.ailingo.server.entity.topic.TopicEntity;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "context")
public class ContextEntity extends BaseEntity {

    private String title;
    private String text;
    private String languageLevel;
    private String description;
    private String translationLink;

    @ManyToOne
    private TopicEntity topic;
}