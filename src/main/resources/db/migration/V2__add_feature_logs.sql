CREATE TABLE IF NOT EXISTS feature_errors
(
    id                        UUID PRIMARY KEY,
    feature_id                UUID NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    timestamp                 TIMESTAMP WITH TIME ZONE NOT NULL,
    trace                     TEXT NOT NULL
);