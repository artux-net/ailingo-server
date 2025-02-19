package net.artux.ailingo.server.jwt.model;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@Getter
public class SecurityUser extends User {

    public SecurityUser(UUID id, UserDetails details) {
        super(details.getUsername(), details.getPassword(), details.getAuthorities());
    }
}
