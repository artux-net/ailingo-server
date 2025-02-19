package net.artux.ailingo.server.model;

import lombok.Data;

@Data
public class ChatEvent {

    private String content;

    public ChatEvent(String content) {
        this.content = content;
    }

    public static ChatEvent of(String content) {
        return new ChatEvent(content);
    }
}