package net.artux.ailingo.server.configuration.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.artux.ailingo.server.entity.user.UserEntity;
import net.artux.ailingo.server.model.ChatUpdate;
import net.artux.ailingo.server.model.MessageDto;
import net.artux.ailingo.server.service.UserService;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;


public abstract class CommonHandler extends SocketHandler {

    private final static ChatUpdate EMPTY_UPDATE = ChatUpdate.empty();

    private final LinkedList<MessageDto> lastMessages;

    public CommonHandler(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper, userService);

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
