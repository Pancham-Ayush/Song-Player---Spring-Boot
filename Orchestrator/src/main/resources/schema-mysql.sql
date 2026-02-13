CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    content LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    type VARCHAR(32) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
