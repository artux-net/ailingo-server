package net.artux.ailingo.server.service.impl;

import lombok.RequiredArgsConstructor;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.SecurityUser;
import net.artux.ailingo.server.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.isBlank())
            throw new UsernameNotFoundException("Access denied.");

        Optional<UserEntity> userOptional;
        if (username.contains("@")) {
            userOptional = userRepository.getByEmail(username);
        } else
            userOptional = userRepository.getByLogin(username);

        if (userOptional.isPresent()) {
            UserEntity simpleUser = userOptional.get();
            if (simpleUser.getLastLoginAt() == null
                    || simpleUser.getLastLoginAt().plusSeconds(5 * 60).isBefore(Instant.now())) {
                simpleUser.setLastLoginAt(Instant.now());
                userRepository.save(simpleUser);
            }
            UserDetails userDetails = User.builder()
                    .username(simpleUser.getLogin())
                    .password(simpleUser.getPassword())
                    .build();
            return new SecurityUser(simpleUser.getId(), userDetails);
        } else {
            logger.error("User with login '" + username + "' not found.");
            throw new UsernameNotFoundException("User not found");
        }
    }
}