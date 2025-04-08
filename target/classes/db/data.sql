-- Insert admin user
INSERT INTO users (username, name, email, password) 
SELECT 'admin', 'Admin User', 'admin@skiconnect.com', '$2a$10$/cz.uUbt8m4OcmvXL3FZVObDXvVLp305GME4rZp.hE0gypD7APYwi'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Insert initial roles
INSERT INTO user_roles (user_id, role) 
SELECT id, 'ROLE_ADMIN' 
FROM users 
WHERE username = 'admin' 
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = users.id AND role = 'ROLE_ADMIN');

-- Insert sample places
INSERT INTO places (name, location) 
SELECT 'Snowpeak Resort', 'North Slope'
WHERE NOT EXISTS (SELECT 1 FROM places WHERE name = 'Snowpeak Resort');

INSERT INTO places (name, location) 
SELECT 'Mountain View', 'South Valley'
WHERE NOT EXISTS (SELECT 1 FROM places WHERE name = 'Mountain View');

-- Insert sample schools (if admin user exists)
INSERT INTO schools (name, place_id, user_id)
SELECT 'Snowpeak Ski School', p.id, u.id
FROM places p, users u
WHERE p.name = 'Snowpeak Resort' 
AND u.username = 'admin'
AND NOT EXISTS (SELECT 1 FROM schools WHERE name = 'Snowpeak Ski School');

-- Insert sample teachers (if school exists)
INSERT INTO teachers (name, is_freelancer, school_id, user_id)
SELECT 'John Doe', false, s.id, u.id
FROM schools s, users u
WHERE s.name = 'Snowpeak Ski School'
AND u.username = 'admin'
AND NOT EXISTS (SELECT 1 FROM teachers WHERE name = 'John Doe'); 