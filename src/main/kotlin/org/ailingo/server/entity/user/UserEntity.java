package org.ailingo.server.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ailingo.server.entity.BaseEntity;
import org.ailingo.server.model.RegisterUserDto;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    private Integer xp = 0;
    private Integer streak = 0;
    private Integer coins = 0;

    @Column(columnDefinition = "boolean default true")
    private Boolean receiveEmails = true;

    private Instant registration;
    private Instant lastLoginAt;
    private Instant lastSession;

    public UserEntity(RegisterUserDto registerUser, PasswordEncoder passwordEncoder) {
        login = registerUser.getLogin();
        password = passwordEncoder.encode(registerUser.getPassword());
        email = registerUser.getEmail();
        name = registerUser.getName();
        avatar = registerUser.getAvatar();
        receiveEmails = true;
        xp = 0;
        streak = 0;
        coins = 0;
        lastLoginAt = registration = Instant.now();
    }
}
