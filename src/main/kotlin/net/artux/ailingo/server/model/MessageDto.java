package net.artux.ailingo.server.model;
import lombok.Data;
import net.artux.ailingo.server.dto.UserDto;

import java.time.Instant;

@Data
public class MessageDto {

    private Type type;
    private UserDto author;
    private String content;
    private Instant timestamp;

    public MessageDto(UserDto author, String content) {
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