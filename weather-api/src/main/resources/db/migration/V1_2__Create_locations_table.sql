CREATE TABLE IF NOT EXISTS locations
(
    id SERIAL PRIMARY KEY,
    city VARCHAR(64) NOT NULL,
    country VARCHAR(64) NOT NULL,
    CONSTRAINT unique_city_country UNIQUE (city, country)
)