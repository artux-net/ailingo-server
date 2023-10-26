package org.ailingo.server.configuration.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ailingo.server.model.UserMapper;
import org.ailingo.server.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class ChatHandler extends CommonHandler {

    public ChatHandler(UserService userService, ObjectMapper objectMapper, UserMapper userMapper) {
        super(userService, objectMapper, userMapper);
    }

}
