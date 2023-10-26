package org.ailingo.server.configuration.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ailingo.server.entity.user.UserEntity;
import org.ailingo.server.model.ChatUpdate;
import org.ailingo.server.model.MessageDTO;
import org.ailingo.server.model.UserMapper;
import org.ailingo.server.user.UserService;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;


public abstract class CommonHandler extends SocketHandler {

    private final static ChatUpdate EMPTY_UPDATE = ChatUpdate.empty();

    private final LinkedList<MessageDTO> lastMessages;

    public CommonHandler(UserService userService, ObjectMapper objectMapper, UserMapper userMapper) {
        super(objectMapper, userService, userMapper);

        lastMessages = new LinkedList<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession userSession) {
        super.afterConnectionEstablished(userSession);
        accept(userSession);

        ChatUpdate initialUpdate = ChatUpdate.of(lastMessages);

        sendUpdate(userSession, initialUpdate);
    }

    @Override
    public void handleMessage(WebSocketSession userSession, WebSocketMessage<?> webSocketMessage) {
        String message = getTextMessage(webSocketMessage);
        UserEntity author = getMember(userSession);

        if (message.length() > 400) {
            sendUpdate(userSession, ChatUpdate.event("Сообщение слишком большое."));
            return;
        }

        if (!message.isBlank()) {
            ChatUpdate update = getUpdate(userSession, message);
            applyUpdate(update);
        } else {
            sendUpdate(userSession, EMPTY_UPDATE);
        }
    }

    protected void applyUpdate(ChatUpdate update) {
        for (WebSocketSession session : getSessions()) {
            sendUpdate(session, update);
        }
        lastMessages.addAll(update.asOld().getUpdates());
    }
}
