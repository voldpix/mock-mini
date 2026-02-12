PRAGMA journal_mode=WAL;

CREATE TABLE IF NOT EXISTS mock_rules (
      id VARCHAR(45) NOT NULL PRIMARY KEY,
      method VARCHAR(20) NOT NULL,
      path VARCHAR NOT NULL,
      headers VARCHAR,
      body VARCHAR,
      status_code INT NOT NULL DEFAULT 200,
      delay INT NOT NULL DEFAULT 0,
      created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mock_rules_timestamp
    ON mock_rules(created_at);