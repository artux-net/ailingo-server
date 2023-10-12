package org.ailingo.server.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_statistic")
@NoArgsConstructor
public class StatisticEntity {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(columnDefinition = "integer default 0")
    public Integer distance = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer killedEnemies = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer killedMutants = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer secretFound = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer collectedArtifacts = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer boughtItems = 0;
    @Column(columnDefinition = "integer default 0")
    public Integer soldItems = 0;

    StatisticEntity(UserEntity user){
        this.user = user;
        id = user.getId();
    }

}
