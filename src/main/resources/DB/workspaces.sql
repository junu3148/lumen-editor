
-- 작업공간 테이블
CREATE TABLE workspaces
(
    workspaces_key BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_key BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_key) REFERENCES user(user_key) ON DELETE CASCADE
);