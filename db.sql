-- ============================================
-- Database: nitrisense_db
-- MySQL 8.0+
-- ============================================

CREATE DATABASE IF NOT EXISTS nitrisense_db
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- drop database nitrisense_db
USE nitrisense_db;
-- INSERT INTO users (user_id, auth_uid, email, display_name) 
-- VALUES (1, 'mock_uid_123', 'test@gmail.com', 'Dev User');
-- DROP TRIGGER IF EXISTS after_food_entry_update;
-- DROP TRIGGER IF EXISTS after_food_entry_delete;
-- DROP TRIGGER IF EXISTS after_food_item_insert;
-- DROP TRIGGER IF EXISTS after_food_item_update;
-- DROP TRIGGER IF EXISTS after_food_item_delete;
-- DROP TRIGGER IF EXISTS trg_food_item_soft_delete;
-- ============================================
-- 1. users (có soft delete, audit)
-- ============================================
CREATE TABLE users (
    user_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    auth_uid VARCHAR(128) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    avatar_url TEXT,
    date_of_birth DATE,
    gender ENUM('male', 'female', 'other'),
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    activity_level ENUM('sedentary', 'light', 'moderate', 'active', 'very_active'),
    daily_calorie_goal INT UNSIGNED,
    water_goal_ml INT UNSIGNED,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT UNSIGNED NULL,
    updated_by INT UNSIGNED NULL,
    INDEX idx_auth_uid (auth_uid),
    INDEX idx_email (email),
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ============================================
-- 2. user_goals
-- ============================================
CREATE TABLE user_goals (
    goal_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    goal_type ENUM('calorie', 'protein', 'carbs', 'fat', 'water', 'sleep_hours', 'steps') NOT NULL,
    target_value DECIMAL(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_goal (user_id, goal_type, start_date)
);

-- ============================================
-- 3. user_reminder_settings
-- ============================================
CREATE TABLE user_reminder_settings (
    setting_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    reminder_type ENUM('water', 'meal', 'sleep', 'vitamin', 'exercise', 'general') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    start_time TIME NULL,
    end_time TIME NULL,
    interval_minutes INT UNSIGNED NULL,
    specific_times JSON NULL,
    message_template VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- 4. device_tokens
-- ============================================
CREATE TABLE device_tokens (
    token_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    fcm_token VARCHAR(255) NOT NULL,
    device_name VARCHAR(100),
    last_used_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_token (user_id, fcm_token)
);

-- ============================================
-- 5. food_items (không dùng calories_per_100g nếu đơn vị không chuẩn)
--    thêm nutrient_basis để xác định cơ sở dinh dưỡng
-- ============================================
CREATE TABLE food_items (
    food_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    barcode VARCHAR(50) UNIQUE,
    category VARCHAR(50),
    serving_size DECIMAL(8,2) NOT NULL DEFAULT 1.00,
    serving_unit VARCHAR(20) NOT NULL,
    servings_per_package DECIMAL(8,2),
    nutrient_basis ENUM('100g', '100ml', 'serving') DEFAULT 'serving', -- cơ sở cho các cột per_basis
    calories DECIMAL(8,2) NOT NULL,        -- per serving
    calories_per_basis DECIMAL(8,2) GENERATED ALWAYS AS (
        CASE 
            WHEN nutrient_basis = 'serving' THEN calories / serving_size
            WHEN nutrient_basis = '100g' AND serving_unit IN ('gram','g') THEN calories / serving_size * 100
            WHEN nutrient_basis = '100ml' AND serving_unit IN ('ml','milliliter') THEN calories / serving_size * 100
            ELSE NULL
        END
    ) STORED,
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    vitamin_a_mcg DECIMAL(8,2),
    vitamin_b12_mcg DECIMAL(8,2),
    vitamin_c_mg DECIMAL(8,2),
    vitamin_d_mcg DECIMAL(8,2),
    iron_mg DECIMAL(8,2),
    calcium_mg DECIMAL(8,2),
    potassium_mg DECIMAL(8,2),
    is_verified BOOLEAN DEFAULT FALSE,
    source ENUM('gemini_ai', 'open_food_facts', 'user_manual', 'barcode') NOT NULL,
    created_by INT UNSIGNED NULL,
    usage_count INT UNSIGNED DEFAULT 0,
    last_used_at TIMESTAMP NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_barcode (barcode),
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_usage (usage_count),
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ============================================
-- 6. food_entries (soft delete, audit)
-- ============================================
CREATE TABLE food_entries (
    entry_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    entry_date DATE NOT NULL,
    meal_type ENUM('breakfast', 'lunch', 'dinner', 'snack') NOT NULL,
    notes TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT UNSIGNED NULL,
    updated_by INT UNSIGNED NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_date_meal (user_id, entry_date, meal_type)
);

-- ============================================
-- 7. food_entry_items (thêm updated_at, is_deleted)
-- ============================================
CREATE TABLE food_entry_items (
    item_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    entry_id INT UNSIGNED NOT NULL,
    food_id INT UNSIGNED NULL,
    custom_name VARCHAR(255),
    serving_qty DECIMAL(8,2) NOT NULL DEFAULT 1.00,
    serving_unit VARCHAR(20),
    calories_per_serving DECIMAL(8,2) NOT NULL,
    calories DECIMAL(8,2) GENERATED ALWAYS AS (serving_qty * calories_per_serving) STORED,
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    vitamin_a_mcg DECIMAL(8,2),
    vitamin_b12_mcg DECIMAL(8,2),
    vitamin_c_mg DECIMAL(8,2),
    vitamin_d_mcg DECIMAL(8,2),
    iron_mg DECIMAL(8,2),
    calcium_mg DECIMAL(8,2),
    potassium_mg DECIMAL(8,2),
    source ENUM('text', 'image', 'barcode', 'manual') NOT NULL,
    raw_input TEXT,
    image_urls JSON,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (entry_id) REFERENCES food_entries(entry_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food_items(food_id) ON DELETE SET NULL,
    INDEX idx_entry (entry_id)
);

-- ============================================
-- 8. nutrient_reference (đã có activity_level, pregnancy)
-- ============================================
CREATE TABLE nutrient_reference (
    ref_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    gender ENUM('male', 'female') NOT NULL,
    activity_level ENUM('sedentary', 'light', 'moderate', 'active', 'very_active') NOT NULL,
    age_min TINYINT UNSIGNED NOT NULL,
    age_max TINYINT UNSIGNED NOT NULL,
    pregnancy BOOLEAN DEFAULT FALSE,
    calories_kcal SMALLINT UNSIGNED,
    protein_g DECIMAL(5,2),
    vitamin_a_mcg SMALLINT UNSIGNED,
    vitamin_b12_mcg DECIMAL(4,2),
    vitamin_c_mg SMALLINT UNSIGNED,
    vitamin_d_mcg DECIMAL(4,2),
    iron_mg DECIMAL(5,2),
    calcium_mg SMALLINT UNSIGNED,
    potassium_mg SMALLINT UNSIGNED,
    water_ml SMALLINT UNSIGNED,
    INDEX idx_age_gender_activity (gender, activity_level, age_min, age_max, pregnancy)
);
-- ============================================
-- 9. sleep_records (có CHECK constraint)
-- ============================================
CREATE TABLE sleep_records (
    sleep_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    sleep_start DATETIME NOT NULL,
    sleep_end DATETIME NOT NULL,
    duration_hours DECIMAL(5,2) GENERATED ALWAYS AS (TIMESTAMPDIFF(MINUTE, sleep_start, sleep_end) / 60) STORED,
    quality ENUM('good', 'fair', 'poor'),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_sleep (user_id, sleep_start),
    CONSTRAINT chk_sleep_end_gt_start CHECK (sleep_end > sleep_start)
);

-- ============================================
-- 10. exercise_tests
-- ============================================
CREATE TABLE exercise_tests (
    test_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    test_date DATE NOT NULL,
    test_type ENUM('pushups', 'situps', 'running_1km', 'plank_seconds', 'jump_squats', 'others') NOT NULL,
    value DECIMAL(8,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_test (user_id, test_date, test_type)
);

-- ============================================
-- 11. water_intake (đổi tên date -> intake_date)
-- ============================================
CREATE TABLE water_intake (
    water_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    intake_date DATE NOT NULL,
    amount_ml INT UNSIGNED NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    source ENUM('manual', 'reminder', 'auto') DEFAULT 'manual',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_water (user_id, intake_date)
);

-- ============================================
-- 12. daily_summaries
-- ============================================
CREATE TABLE daily_summaries (
    summary_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    summary_date DATE NOT NULL,
    total_calories INT UNSIGNED DEFAULT 0,
    total_protein_g DECIMAL(8,2) DEFAULT 0,
    total_carbs_g DECIMAL(8,2) DEFAULT 0,
    total_fat_g DECIMAL(8,2) DEFAULT 0,
    total_water_ml INT UNSIGNED DEFAULT 0,
    sleep_hours DECIMAL(4,2) DEFAULT 0,
    num_meals TINYINT UNSIGNED DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, summary_date)
);

-- ============================================
-- 13. nutrients (bảng chuẩn hóa cho daily_deficits)
-- ============================================
CREATE TABLE nutrients (
    nutrient_id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nutrient_name VARCHAR(50) NOT NULL UNIQUE,
    unit VARCHAR(20)
);

INSERT INTO nutrients (nutrient_name, unit) VALUES
('calories', 'kcal'),
('protein', 'g'),
('vitaminA', 'mcg'),
('vitaminB12', 'mcg'),
('vitaminC', 'mg'),
('vitaminD', 'mcg'),
('iron', 'mg'),
('calcium', 'mg'),
('potassium', 'mg'),
('water', 'ml');

-- ============================================
-- 14. daily_deficits (dùng khóa ngoại tới nutrients)
-- ============================================
CREATE TABLE daily_deficits (
    deficit_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    summary_id INT UNSIGNED NOT NULL,
    nutrient_id TINYINT UNSIGNED NOT NULL,
    deficit_amount DECIMAL(8,2),   -- mức thiếu hụt (có thể NULL nếu chỉ cần biết thiếu)
    FOREIGN KEY (summary_id) REFERENCES daily_summaries(summary_id) ON DELETE CASCADE,
    FOREIGN KEY (nutrient_id) REFERENCES nutrients(nutrient_id),
    INDEX idx_summary (summary_id),
    UNIQUE KEY unique_summary_nutrient (summary_id, nutrient_id)
);

-- ============================================
-- 15. reports (thêm total_days, compliance_score)
-- ============================================
CREATE TABLE reports (
    report_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days TINYINT UNSIGNED NOT NULL,
    compliance_score DECIMAL(5,2) DEFAULT 0,
    avg_calories DECIMAL(8,2),
    avg_water_ml DECIMAL(8,2),
    summary_text TEXT NOT NULL,
    recommendation TEXT,
    chart_image_url TEXT,
    shared_count TINYINT UNSIGNED DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_report (user_id, start_date),
    CONSTRAINT chk_total_days CHECK (total_days = DATEDIFF(end_date, start_date) + 1)
);

-- ============================================
-- 16. share_logs
-- ============================================
CREATE TABLE share_logs (
    share_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id INT UNSIGNED NOT NULL,
    shared_to_email VARCHAR(255) NOT NULL,
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(report_id) ON DELETE CASCADE
);

-- ============================================
-- 17. schedules (soft delete)
-- ============================================
CREATE TABLE schedules (
    schedule_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    schedule_date DATE NOT NULL,
    event_type ENUM('meal', 'exercise', 'study', 'rest', 'water_reminder', 'sleep') NOT NULL,
    title VARCHAR(100),
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    notes TEXT,
    is_user_modified BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_schedule (user_id, schedule_date)
);
ALTER TABLE schedules 
ADD COLUMN is_completed BOOLEAN DEFAULT FALSE AFTER is_deleted;
-- ============================================
-- 18. schedule_templates
-- ============================================
CREATE TABLE schedule_templates (
    template_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    day_of_week TINYINT UNSIGNED NOT NULL,
    event_type ENUM('meal', 'exercise', 'study', 'rest', 'water_reminder', 'sleep') NOT NULL,
    title VARCHAR(100),
    start_time TIME NOT NULL,
    end_time TIME,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_template (user_id, day_of_week, is_active)
);

-- ============================================
-- 19. ai_processing_logs
-- ============================================
CREATE TABLE ai_processing_logs (
    log_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    input_type ENUM('text', 'image', 'barcode') NOT NULL,
    raw_input TEXT NOT NULL,
    ai_response_json JSON,
    confidence_score DECIMAL(3,2),
    parsed_nutrients JSON,
    error_message TEXT,
    processing_time_ms INT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_ai (user_id, created_at)
);

-- ============================================
-- 20. scan_history
-- ============================================
CREATE TABLE scan_history (
    scan_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    scan_type ENUM('barcode', 'image') NOT NULL,
    scan_value VARCHAR(255),
    result_food_id INT UNSIGNED NULL,
    is_success BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (result_food_id) REFERENCES food_items(food_id) ON DELETE SET NULL,
    INDEX idx_user_scan (user_id, created_at)
);

-- ============================================
-- 21. favorite_foods
-- ============================================
CREATE TABLE favorite_foods (
    user_id INT UNSIGNED NOT NULL,
    food_id INT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, food_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food_items(food_id) ON DELETE CASCADE
);

-- ============================================
-- 22. streaks
-- ============================================
CREATE TABLE streaks (
    streak_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    streak_type ENUM('login', 'water_goal', 'calorie_goal', 'sleep_goal', 'exercise') NOT NULL,
    current_streak INT UNSIGNED DEFAULT 0,
    longest_streak INT UNSIGNED DEFAULT 0,
    last_updated_date DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_streak (user_id, streak_type)
);

-- ============================================
-- 23. achievements
-- ============================================
CREATE TABLE achievements (
    achievement_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    badge_icon_url TEXT,
    requirement_type ENUM('days_streak', 'total_calories', 'total_water', 'exercise_count', 'meal_count'),
    requirement_value INT UNSIGNED NOT NULL,
    points INT UNSIGNED DEFAULT 0
);

-- ============================================
-- 24. user_achievements
-- ============================================
CREATE TABLE user_achievements (
    user_id INT UNSIGNED NOT NULL,
    achievement_id INT UNSIGNED NOT NULL,
    achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(achievement_id) ON DELETE CASCADE
);

-- ============================================
-- 25. chat_messages
-- Lưu lịch sử trò chuyện giữa user - AI - system
-- ============================================
CREATE TABLE chat_messages (
    message_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,

    sender ENUM('USER', 'AI', 'SYSTEM') NOT NULL,
    message_type ENUM('CHAT', 'FOLLOWUP', 'REMINDER', 'ACHIEVEMENT', 'WARNING', 'SYSTEM_NOTICE') 
        NOT NULL DEFAULT 'CHAT',

    message_text TEXT NOT NULL,
    context_data JSON NULL,

    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP NULL DEFAULT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,

    INDEX idx_user_chat_time (user_id, created_at),
    INDEX idx_user_chat_unread (user_id, is_read, created_at),
    INDEX idx_user_chat_type (user_id, message_type)
);

-- ============================================
-- TRIGGERS
-- ============================================

-- Trigger AFTER INSERT trên food_entries: tăng num_meals (nếu chưa có summary)
DELIMITER $$
CREATE TRIGGER after_food_entry_insert
AFTER INSERT ON food_entries
FOR EACH ROW
BEGIN
    INSERT INTO daily_summaries (user_id, summary_date, num_meals)
    VALUES (NEW.user_id, NEW.entry_date, 1)
    ON DUPLICATE KEY UPDATE
        num_meals = num_meals + 1;
END$$

-- Trigger AFTER UPDATE trên food_entries: xử lý khi thay đổi ngày hoặc soft delete
CREATE TRIGGER after_food_entry_update
AFTER UPDATE ON food_entries
FOR EACH ROW
BEGIN
    -- Nếu ngày thay đổi: giảm bữa cũ, tăng bữa mới
    IF OLD.entry_date != NEW.entry_date AND OLD.is_deleted = FALSE AND NEW.is_deleted = FALSE THEN
        UPDATE daily_summaries SET num_meals = num_meals - 1
        WHERE user_id = OLD.user_id AND summary_date = OLD.entry_date;
        INSERT INTO daily_summaries (user_id, summary_date, num_meals)
        VALUES (NEW.user_id, NEW.entry_date, 1)
        ON DUPLICATE KEY UPDATE num_meals = num_meals + 1;
    END IF;
    -- Nếu soft delete: giảm num_meals
    IF OLD.is_deleted = FALSE AND NEW.is_deleted = TRUE THEN
        UPDATE daily_summaries SET num_meals = num_meals - 1
        WHERE user_id = OLD.user_id AND summary_date = OLD.entry_date;
    END IF;
    -- Nếu khôi phục: tăng num_meals
    IF OLD.is_deleted = TRUE AND NEW.is_deleted = FALSE THEN
        INSERT INTO daily_summaries (user_id, summary_date, num_meals)
        VALUES (NEW.user_id, NEW.entry_date, 1)
        ON DUPLICATE KEY UPDATE num_meals = num_meals + 1;
    END IF;
END$$

-- Trigger AFTER DELETE trên food_entries: giảm num_meals
CREATE TRIGGER after_food_entry_delete
AFTER DELETE ON food_entries
FOR EACH ROW
BEGIN
    IF OLD.is_deleted = FALSE THEN
        UPDATE daily_summaries SET num_meals = num_meals - 1
        WHERE user_id = OLD.user_id AND summary_date = OLD.entry_date;
    END IF;
END$$

-- Trigger AFTER INSERT trên food_entry_items: cập nhật tổng dinh dưỡng
CREATE TRIGGER after_food_item_insert
AFTER INSERT ON food_entry_items
FOR EACH ROW
BEGIN
    DECLARE v_user_id INT UNSIGNED;
    DECLARE v_entry_date DATE;
    SELECT user_id, entry_date INTO v_user_id, v_entry_date
    FROM food_entries WHERE entry_id = NEW.entry_id AND is_deleted = FALSE;
    
    IF v_user_id IS NOT NULL THEN
        INSERT INTO daily_summaries (user_id, summary_date, total_calories, total_protein_g, total_carbs_g, total_fat_g)
        VALUES (v_user_id, v_entry_date, NEW.calories, NEW.protein_g, NEW.carbs_g, NEW.fat_g)
        ON DUPLICATE KEY UPDATE
            total_calories = total_calories + NEW.calories,
            total_protein_g = total_protein_g + NEW.protein_g,
            total_carbs_g = total_carbs_g + NEW.carbs_g,
            total_fat_g = total_fat_g + NEW.fat_g;
    END IF;
END$$

-- Trigger AFTER UPDATE trên food_entry_items
CREATE TRIGGER after_food_item_update
AFTER UPDATE ON food_entry_items
FOR EACH ROW
BEGIN
    DECLARE v_user_id INT UNSIGNED;
    DECLARE v_entry_date DATE;
    SELECT user_id, entry_date INTO v_user_id, v_entry_date
    FROM food_entries WHERE entry_id = NEW.entry_id AND is_deleted = FALSE;
    
    IF v_user_id IS NOT NULL THEN
        -- Trừ giá trị cũ, cộng giá trị mới
        UPDATE daily_summaries
        SET total_calories = total_calories - OLD.calories + NEW.calories,
            total_protein_g = total_protein_g - OLD.protein_g + NEW.protein_g,
            total_carbs_g = total_carbs_g - OLD.carbs_g + NEW.carbs_g,
            total_fat_g = total_fat_g - OLD.fat_g + NEW.fat_g
        WHERE user_id = v_user_id AND summary_date = v_entry_date;
    END IF;
END$$

-- Trigger AFTER DELETE trên food_entry_items
CREATE TRIGGER after_food_item_delete
AFTER DELETE ON food_entry_items
FOR EACH ROW
BEGIN
    DECLARE v_user_id INT UNSIGNED;
    DECLARE v_entry_date DATE;
    SELECT user_id, entry_date INTO v_user_id, v_entry_date
    FROM food_entries WHERE entry_id = OLD.entry_id AND is_deleted = FALSE;
    
    IF v_user_id IS NOT NULL THEN
        UPDATE daily_summaries
        SET total_calories = total_calories - OLD.calories,
            total_protein_g = total_protein_g - OLD.protein_g,
            total_carbs_g = total_carbs_g - OLD.carbs_g,
            total_fat_g = total_fat_g - OLD.fat_g
        WHERE user_id = v_user_id AND summary_date = v_entry_date;
    END IF;
END$$

-- Trigger cho water_intake
CREATE TRIGGER after_water_insert
AFTER INSERT ON water_intake
FOR EACH ROW
BEGIN
    INSERT INTO daily_summaries (user_id, summary_date, total_water_ml)
    VALUES (NEW.user_id, NEW.intake_date, NEW.amount_ml)
    ON DUPLICATE KEY UPDATE total_water_ml = total_water_ml + NEW.amount_ml;
END$$

CREATE TRIGGER after_water_update
AFTER UPDATE ON water_intake
FOR EACH ROW
BEGIN
    UPDATE daily_summaries
    SET total_water_ml = total_water_ml - OLD.amount_ml + NEW.amount_ml
    WHERE user_id = NEW.user_id AND summary_date = NEW.intake_date;
END$$

CREATE TRIGGER after_water_delete
AFTER DELETE ON water_intake
FOR EACH ROW
BEGIN
    UPDATE daily_summaries
    SET total_water_ml = total_water_ml - OLD.amount_ml
    WHERE user_id = OLD.user_id AND summary_date = OLD.intake_date;
END$$

-- Trigger cho sleep_records
CREATE TRIGGER after_sleep_insert
AFTER INSERT ON sleep_records
FOR EACH ROW
BEGIN
    INSERT INTO daily_summaries (user_id, summary_date, sleep_hours)
    VALUES (NEW.user_id, DATE(NEW.sleep_start), NEW.duration_hours)
    ON DUPLICATE KEY UPDATE sleep_hours = NEW.duration_hours; -- chỉ lấy giấc ngủ chính (có thể cải tiến)
END$$

CREATE TRIGGER after_sleep_update
AFTER UPDATE ON sleep_records
FOR EACH ROW
BEGIN
    UPDATE daily_summaries
    SET sleep_hours = NEW.duration_hours
    WHERE user_id = NEW.user_id AND summary_date = DATE(NEW.sleep_start);
END$$

CREATE TRIGGER after_sleep_delete
AFTER DELETE ON sleep_records
FOR EACH ROW
BEGIN
    UPDATE daily_summaries
    SET sleep_hours = 0
    WHERE user_id = OLD.user_id AND summary_date = DATE(OLD.sleep_start);
END$$

DELIMITER ;

-- ============================================
-- Dữ liệu mẫu cho nutrient_reference
-- ============================================
INSERT INTO nutrient_reference (gender, activity_level, age_min, age_max, calories_kcal, protein_g, vitamin_c_mg, iron_mg, calcium_mg, water_ml) VALUES
('female', 'moderate', 19, 30, 2000, 46, 75, 18, 1000, 2700),
('male', 'moderate', 19, 30, 2400, 56, 90, 8, 1000, 3000);\

-- Thêm các món giàu PROTEIN
INSERT INTO food_items (name, calories, protein_g, carbs_g, fat_g, fiber_g, vitamin_a_mcg, vitamin_b12_mcg, vitamin_c_mg, vitamin_d_mcg, iron_mg, calcium_mg, potassium_mg, is_deleted, source, created_by, serving_size, serving_unit)
VALUES 
('Ức gà luộc', 165, 31.0, 0.0, 3.6, 0.0, 0, 0.3, 0, 0, 1.0, 15.0, 255.0, 0, 'user_manual', 1, 100, 'g'),
('Trứng gà luộc', 155, 13.0, 1.1, 11.0, 0.0, 149, 1.1, 0, 2.2, 1.2, 50.0, 126.0, 0, 'user_manual', 1, 100, 'g'),
('Sữa chua Hy Lạp', 59, 10.0, 3.6, 0.4, 0.0, 0, 0.7, 0, 0, 0.1, 110.0, 141.0, 0, 'user_manual', 1, 100, 'g');

-- Thêm các món giàu VITAMIN C
INSERT INTO food_items (name, calories, protein_g, carbs_g, fat_g, fiber_g, vitamin_a_mcg, vitamin_b12_mcg, vitamin_c_mg, vitamin_d_mcg, iron_mg, calcium_mg, potassium_mg, is_deleted, source, created_by, serving_size, serving_unit)
VALUES 
('Quả Ổi', 68, 2.6, 14.3, 1.0, 5.4, 31, 0, 228.3, 0, 0.3, 18.0, 417.0, 0, 'user_manual', 1, 100, 'g'),
('Quả Cam', 47, 0.9, 11.8, 0.1, 2.4, 11, 0, 53.2, 0, 0.1, 40.0, 181.0, 0, 'user_manual', 1, 100, 'g'),
('Bông cải xanh (Súp lơ) luộc', 35, 2.4, 7.2, 0.4, 3.3, 31, 0, 89.2, 0, 0.7, 47.0, 316.0, 0, 'user_manual', 1, 100, 'g');

-- Thêm các món giàu CANXI
INSERT INTO food_items (name, calories, protein_g, carbs_g, fat_g, fiber_g, vitamin_a_mcg, vitamin_b12_mcg, vitamin_c_mg, vitamin_d_mcg, iron_mg, calcium_mg, potassium_mg, is_deleted, source, created_by, serving_size, serving_unit)
VALUES 
('Sữa tươi không đường', 42, 3.4, 4.8, 1.0, 0.0, 46, 0.4, 0, 1.0, 0.0, 120.0, 150.0, 0, 'user_manual', 1, 100, 'ml'),
('Phô mai bò cười', 239, 11.0, 1.5, 21.0, 0.0, 200, 1.5, 0, 0, 0.5, 600.0, 100.0, 0, 'user_manual', 1, 100, 'g'),
('Rau dền luộc', 23, 2.5, 4.0, 0.2, 2.0, 146, 0, 43.3, 0, 2.3, 215.0, 340.0, 0, 'user_manual', 1, 100, 'g');

-- Thêm các món giàu SẮT
INSERT INTO food_items (name, calories, protein_g, carbs_g, fat_g, fiber_g, vitamin_a_mcg, vitamin_b12_mcg, vitamin_c_mg, vitamin_d_mcg, iron_mg, calcium_mg, potassium_mg, is_deleted, source, created_by, serving_size, serving_unit)
VALUES 
('Thịt bò nạc', 250, 26.0, 0.0, 15.0, 0.0, 0, 2.6, 0, 0, 2.6, 18.0, 318.0, 0, 'user_manual', 1, 100, 'g'),
('Gan lợn (heo)', 165, 26.0, 3.8, 4.4, 0.0, 6500, 25.3, 25.3, 0, 18.0, 9.0, 273.0, 0, 'user_manual', 1, 100, 'g');


-- set sql_safe_updates = 1
-- delete from food_items wher
-- delete from food_entry_items
select * from users;
select * from daily_summaries;
select * from food_entry_items;
select * from user_goals;