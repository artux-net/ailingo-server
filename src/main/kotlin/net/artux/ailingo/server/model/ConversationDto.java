package net.artux.ailingo.server.model;

import java.util.UUID;

public interface ConversationDto {
    UUID getConversationId();
    String getTopicName();
}