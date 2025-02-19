CREATE TABLE message_history
(
    id                 BIGSERIAL PRIMARY KEY       NOT NULL,
    conversation_owner UUID                        NOT NULL,
    topic_id           BIGINT                        NOT NULL,
    conversation_id    UUID                        NOT NULL,
    type               VARCHAR(255),
    user_id            UUID                        NOT NULL,
    content            TEXT                        NOT NULL,
    timestamp          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_message_history_owner FOREIGN KEY (conversation_owner) REFERENCES app_user (id),
    CONSTRAINT fk_message_history_topic FOREIGN KEY (topic_id) REFERENCES topic (id),
    CONSTRAINT fk_message_history_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);