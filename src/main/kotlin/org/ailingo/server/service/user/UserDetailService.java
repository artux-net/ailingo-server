package org.ailingo.server.service.user;

import lombok.RequiredArgsConstructor;
import org.ailingo.server.entity.user.SimpleUser;
import org.ailingo.server.model.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.isBlank())
            throw new UsernameNotFoundException("Access denied.");

        Optional<SimpleUser> userOptional;
        if (username.contains("@")) {
            userOptional = userRepository.getByEmail(username);
        } else
            userOptional = userRepository.getByLogin(username);

        if (userOptional.isPresent()) {
            SimpleUser simpleUser = userOptional.get();
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
