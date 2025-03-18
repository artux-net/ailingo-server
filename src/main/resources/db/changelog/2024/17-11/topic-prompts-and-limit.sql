ALTER TABLE topic
    ADD COLUMN welcome_prompt TEXT,
    ADD COLUMN system_prompt  TEXT,
    ADD COLUMN message_limit  INTEGER;