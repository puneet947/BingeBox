-- Insert sample cities
INSERT INTO cities (name, state, country_code, is_active, created_at, updated_at) VALUES
('Mumbai', 'Maharashtra', 'IN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Delhi', 'Delhi', 'IN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bangalore', 'Karnataka', 'IN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chennai', 'Tamil Nadu', 'IN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Hyderabad', 'Telangana', 'IN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample movies
INSERT INTO movies (title, description, duration_minutes, language, genre, release_date, rating, is_active, created_at, updated_at) VALUES
('Dangal', 'A biographical sports drama about wrestler Mahavir Singh Phogat and his daughters.', 161, 'Hindi', 'DRAMA', '2016-12-21 00:00:00', 8.4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Baahubali: The Beginning', 'An epic historical fiction film about two brothers competing for the throne.', 159, 'Telugu', 'ACTION', '2015-07-10 00:00:00', 8.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('3 Idiots', 'A comedy drama about three engineering students and their college life.', 170, 'Hindi', 'COMEDY', '2009-12-25 00:00:00', 8.4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('KGF: Chapter 1', 'An action film about a young man who rises to become a powerful gangster.', 135, 'Kannada', 'ACTION', '2018-12-21 00:00:00', 8.2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RRR', 'A fictional story about two Indian revolutionaries fighting against British rule.', 187, 'Telugu', 'ACTION', '2022-03-25 00:00:00', 8.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Associate movies with cities (many-to-many relationship)
INSERT INTO movie_cities (movie_id, city_id) VALUES
-- Dangal available in Mumbai, Delhi, Bangalore
(1, 1), (1, 2), (1, 3),
-- Baahubali available in all cities
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
-- 3 Idiots available in Mumbai, Delhi, Chennai
(3, 1), (3, 2), (3, 4),
-- KGF available in Bangalore, Hyderabad, Chennai
(4, 3), (4, 4), (4, 5),
-- RRR available in all cities
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5);
