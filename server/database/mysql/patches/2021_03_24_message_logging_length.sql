ALTER TABLE `chat_logs`
    MODIFY COLUMN `_PREFIX_message` TEXT NOT NULL;

ALTER TABLE `private_message_logs`
    MODIFY COLUMN `_PREFIX_message` TEXT NOT NULL;
