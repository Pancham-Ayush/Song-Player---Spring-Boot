CREATE TABLE IF NOT EXISTS USER_CHAT_HISTORY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(100) NOT NULL,
    chat_id   VARCHAR(100) NOT NULL,
    message   TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_chat (user_id, chat_id),
    INDEX idx_user_chat (user_id, chat_id)
    );
