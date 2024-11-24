ALTER TABLE topic
    ADD COLUMN welcome_prompt VARCHAR(255),
    ADD COLUMN system_prompt  TEXT,
    ADD COLUMN message_limit  INTEGER;
