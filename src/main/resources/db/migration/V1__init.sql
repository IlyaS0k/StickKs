CREATE TABLE IF NOT EXISTS features
(
    id                        UUID PRIMARY KEY,
    name                      VARCHAR(255)             NOT NULL,
    code                      TEXT            NOT NULL,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_success_execution_at TIMESTAMP WITH TIME ZONE,
    success_executions_amount BIGINT DEFAULT 0,
    failed_executions_amount  BIGINT DEFAULT 0,
    last_failed_execution_at  TIMESTAMP WITH TIME ZONE,
    version                   BIGINT DEFAULT 0
);