CREATE TABLE IF NOT EXISTS sensors
(
    id SERIAL PRIMARY KEY,
    model VARCHAR(32) NOT NULL,
    location_id INT,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),
    FOREIGN KEY (location_id) REFERENCES locations (id)
)