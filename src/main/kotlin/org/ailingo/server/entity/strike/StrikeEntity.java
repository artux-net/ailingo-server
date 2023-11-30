package org.ailingo.server.entity.strike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;
import org.ailingo.server.entity.user.UserEntity;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "strike")
public class StrikeEntity extends BaseEntity {

    private Date date;

    @ManyToOne
    private UserEntity user;
}
