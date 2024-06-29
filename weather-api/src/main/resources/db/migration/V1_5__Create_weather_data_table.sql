CREATE TABLE IF NOT EXISTS weather_data
(
    id SERIAL PRIMARY KEY,
    temperature DOUBLE PRECISION NOT NULL,
    wind_speed DOUBLE PRECISION NOT NULL,
    wind_direction VARCHAR(16) NOT NULL,
    humidity DOUBLE PRECISION NOT NULL,
    description VARCHAR(32) NOT NULL,
    sensor_id INT,
    location_id INT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (sensor_id) REFERENCES sensors (id),
    FOREIGN KEY (location_id) REFERENCES locations (id)
)