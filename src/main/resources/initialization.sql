INSERT INTO currency (code, full_name, sign)
VALUES ('USD', 'United States Dollar', '$'),
       ('AUD', 'Australian Dollar', 'A$'),
       ('RUB', 'Russian Ruble', '₽'),
       ('EUR', 'Euro', '€'),
       ('JPY', 'Japanese Yen', '¥');


INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate)
VALUES (1, 2, 1.50),
       (1, 3, 90.44),
       (1, 4, 0.92),
       (1, 5, 157.26),
       (2, 3, 100.89);
