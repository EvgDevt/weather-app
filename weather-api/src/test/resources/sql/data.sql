TRUNCATE TABLE weather_data, sensors, user_locations, locations, users RESTART IDENTITY CASCADE;

-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE locations_id_seq RESTART WITH 1;
-- ALTER SEQUENCE sensors_id_seq RESTART WITH 1;
-- ALTER SEQUENCE weather_data_id_seq RESTART WITH 1;
-- ALTER SEQUENCE user_locations_id_seq RESTART WITH 1;

INSERT INTO users (email, password, firstname, lastname, role, created_at, modified_at, created_by, modified_by)
VALUES
    ('john.doe@example.com', 'password123', 'John', 'Doe', 'ADMIN', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('jane.smith@example.com', 'password123', 'Jane', 'Smith', 'USER', NOW(), NOW(), 'SYSTEM', 'SYSTEM');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO locations (city, country)
VALUES
    ('New York', 'USA'),
    ('Los Angeles', 'USA'),
    ('London', 'UK');

INSERT INTO sensors (model, location_id, created_at, modified_at, created_by, modified_by)
VALUES
    ('SHT31', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('BME280', 2, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
    ('DHT22', 3, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

INSERT INTO user_locations (user_id, location_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 3);

INSERT INTO weather_data (temperature, wind_speed, wind_direction, humidity, description, sensor_id, location_id, created_at)
VALUES
    (22.5, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW()),
    (21.5, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '1 day'),
    (22.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '2 day'),
    (24.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '3 day'),
    (20.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '4 day'),
    (22.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '5 day'),
    (22.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '6 day'),
    (20.0, 5.5, 'NW', 55.0, 'SUNNY', 1, 1, NOW() - interval '7 day'),
    (18.0, 3.0, 'N', 65.0, 'RAIN', 2, 2, NOW()),
    (15.0, 7.5, 'W', 70.0, 'CLOUDY', 3, 3, NOW());