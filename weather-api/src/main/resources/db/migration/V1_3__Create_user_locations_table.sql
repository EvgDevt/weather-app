CREATE TABLE IF NOT EXISTS user_locations
(
    id SERIAL PRIMARY KEY,
    user_id INT,
    location_id INT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (location_id) REFERENCES locations (id)
)