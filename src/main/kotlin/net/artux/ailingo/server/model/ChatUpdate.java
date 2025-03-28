package net.artux.ailingo.server.model;

import lombok.Data;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Data
public class ChatUpdate {

    private List<MessageDto> updates;
    private List<ChatEvent> events;
    private Instant timestamp;

    public ChatUpdate(List<MessageDto> updates, List<ChatEvent> events) {
        this.updates = updates;
        this.events = events;
        this.timestamp = Instant.now();
    }

    public static ChatUpdate of(List<MessageDto> messages) {
        return new ChatUpdate(messages, new LinkedList<>());
    }

    public static ChatUpdate of(MessageDto message) {
        return new ChatUpdate(Collections.singletonList(message), new LinkedList<>());
    }

    public static ChatUpdate event(String event) {
        return new ChatUpdate(Collections.emptyList(), Collections.singletonList(ChatEvent.of(event)));
    }

    public static ChatUpdate empty() {
        return new ChatUpdate(Collections.emptyList(), Collections.emptyList());
    }

    public void addEvent(ChatEvent event) {
        events.add(event);
    }

    public ChatUpdate asOld() {
        for (MessageDto message : updates) {
            message.setType(MessageDto.Type.OLD);
        }
        return this;
    }
}