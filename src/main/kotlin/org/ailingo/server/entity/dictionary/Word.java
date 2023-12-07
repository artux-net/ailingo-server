package org.ailingo.server.entity.dictionary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;
import org.ailingo.server.entity.user.UserEntity;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "word")
public class Word extends BaseEntity{

    private String word;
    private String locale;

    @ManyToOne
    private UserEntity user;
}
