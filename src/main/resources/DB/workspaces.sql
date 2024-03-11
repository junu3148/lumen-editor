-- 작업공간 테이블
CREATE TABLE workspaces
(
    workspaces_key BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_key       BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_key) REFERENCES user (user_key) ON DELETE CASCADE
);

-- 프로젝트 테이블

CREATE TABLE projects
(
    project_key       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_name      VARCHAR(255)    NOT NULL,
    disclosure_status ENUM ('Y', 'N') NOT NULL DEFAULT 'N',
    project_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    workspaces_key    BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (workspaces_key) REFERENCES workspaces (workspaces_key) ON DELETE CASCADE
);