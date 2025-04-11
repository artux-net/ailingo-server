package net.artux.ailingo.server.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.artux.ailingo.server.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private Instant lastStreakAt;
    private Integer coins = 0;

    @Column(columnDefinition = "boolean default true")
    private Boolean receiveEmails = true;

    private Instant registration;
    private Instant lastLoginAt;
    private Instant lastSession;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String verificationCode;

    @Column
    private boolean isEmailVerified = false;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public UserEntity(UUID id, String login, String email, String password, String name, String avatar) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.receiveEmails = true;
        this.xp = 0;
        this.streak = 0;
        this.lastStreakAt = null;
        this.coins = 500;
        this.role = Role.USER;
        this.lastLoginAt = registration = Instant.now();
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedTopicsEntity> savedTopics = new HashSet<>();

    @ElementCollection
    @Column
    private List<String> favoriteWords = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void changeCoins(int amount) {
        this.coins += amount;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}