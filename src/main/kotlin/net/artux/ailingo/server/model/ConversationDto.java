package net.artux.ailingo.server.model;

import java.time.Instant;
import java.util.UUID;

public interface ConversationDto {
    UUID getConversationId();
    String getTopicName();
    Instant getCreationTimestamp();
}