-- Additional indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_lesson_availability_date ON lesson_availabilities (start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_lesson_availability_status ON lesson_availabilities (is_available);
CREATE INDEX IF NOT EXISTS idx_place_name ON places (name);
CREATE INDEX IF NOT EXISTS idx_school_name ON schools (name);
CREATE INDEX IF NOT EXISTS idx_teacher_name ON teachers (name);

-- Additional constraints if needed
ALTER TABLE IF EXISTS lesson_availabilities ADD CONSTRAINT IF NOT EXISTS check_dates 
    CHECK (end_time > start_time);

-- Comments for documentation
COMMENT ON TABLE lesson_availabilities IS 'Stores available time slots for ski lessons';
COMMENT ON TABLE places IS 'Ski resorts and locations where lessons are offered';
COMMENT ON TABLE schools IS 'Ski schools that offer lessons';
COMMENT ON TABLE teachers IS 'Ski instructors who teach lessons'; 