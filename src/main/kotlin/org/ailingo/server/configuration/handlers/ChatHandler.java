package org.ailingo.server.configuration.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.artux.pdanetwork.models.communication.MessageMapper;
import net.artux.pdanetwork.models.user.UserMapper;
import net.artux.pdanetwork.service.user.UserService;
import net.artux.pdanetwork.service.user.ban.BanService;
import net.artux.pdanetwork.service.util.ValuesService;
import org.springframework.stereotype.Service;

@Service
public class ChatHandler extends CommonHandler {

    public ChatHandler(UserService userService, ObjectMapper objectMapper,
                       UserMapper userMapper) {
        super(userService, objectMapper, messageMapper, valuesService, banService, userMapper);
    }

}
