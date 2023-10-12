package org.ailingo.server.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class UserEntity extends BaseEntity {

    @Column(unique = true)
    private String login;
    @Column(unique = true)
    private String email;

    private String password;

    private String name;
    private String avatar;

    private Integer xp;
    private Integer streak;
    private Integer coins;
    @Column(columnDefinition = "boolean default true")
    private Boolean receiveEmails = true;

    private Instant registration;
    private Instant lastLoginAt;
    private Instant lastSession;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private StatisticEntity statistic;

}
