package net.artux.ailingo.server.model;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@Getter
public class SecurityUser extends User {

    private final UUID id;

    public SecurityUser(UUID id, UserDetails details) {
        super(details.getUsername(), details.getPassword(), details.getAuthorities());
        this.id = id;
    }
}
