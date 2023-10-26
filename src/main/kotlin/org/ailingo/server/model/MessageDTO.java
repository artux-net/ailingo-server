package org.ailingo.server.model;

import lombok.Data;

import java.time.Instant;

@Data
public class MessageDTO {

    private Type type;
    private UserDto author;
    private String content;
    private Instant timestamp;

    public MessageDTO(UserDto author, String content) {
        type = Type.NEW;
        this.author = author;
        this.content = content;
        timestamp = Instant.now();
    }

    public enum Type {
        OLD,
        NEW,
        UPDATE,
        DELETE
    }

}
