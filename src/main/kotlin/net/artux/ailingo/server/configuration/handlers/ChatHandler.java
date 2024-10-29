package net.artux.ailingo.server.configuration.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.artux.ailingo.server.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class ChatHandler extends CommonHandler {

    public ChatHandler(UserService userService, ObjectMapper objectMapper) {
        super(userService, objectMapper);
    }

}
