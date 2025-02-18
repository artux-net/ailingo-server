package net.artux.ailingo.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.artux.ailingo.server.model.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private String avatar;

    @Column
    private int xp = 0;

    @Column
    private int streak = 0;

    @Column
    private int coins = 0;

    @Column
    private Instant registration;

    @Column
    private Instant lastLoginAt;

    @Column
    private Instant lastSession;

    @Column(columnDefinition = "boolean default false")
    private boolean receiveEmails = false;

    @Column
    private String verificationCode;

    @Column
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role = Role.USER;

    @ElementCollection
    @Column
    private List<String> favoriteWords = new java.util.ArrayList<>();

    public UserEntity() {
        this(null, null, null, null, null, null);
    }

    public UserEntity(Long id, String login, String email, String password, String name, String avatar) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getFavoriteWords() {
        return favoriteWords;
    }

    public void setFavoriteWords(List<String> favoriteWords) {
        this.favoriteWords = favoriteWords;
    }

    @Override
    public Collection<? extends SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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

    public void changeCoins(int amount) {
        this.coins += amount;
    }
}