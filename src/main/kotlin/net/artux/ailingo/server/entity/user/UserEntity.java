package net.artux.ailingo.server.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.artux.ailingo.server.entity.ChatHistoryEntity;
import net.artux.ailingo.server.entity.BaseEntity;
import net.artux.ailingo.server.model.RegisterUserDto;
import net.artux.ailingo.server.entity.SavedTopicsEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class UserEntity extends BaseEntity implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    private Role role;

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


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedTopicsEntity> savedTopics = new HashSet<>();

    @ElementCollection
    private Set<String> favoriteWords = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatHistoryEntity> chatHistory = new ArrayList<>();

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void removeCoins(int amount) {
        this.coins -= amount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
