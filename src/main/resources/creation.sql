PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS currency
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    sign      VARCHAR(10)  NOT NULL
);

CREATE TABLE IF NOT EXISTS exchange_rate
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id   INTEGER,
    target_currency_id INTEGER,
    rate               DECIMAL(8, 6),
    FOREIGN KEY (base_currency_id) REFERENCES currency (id) ON DELETE CASCADE,
    FOREIGN KEY (target_currency_id) REFERENCES currency (id) ON DELETE CASCADE
);
