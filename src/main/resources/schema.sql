-- questlog 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS questlog;
USE questlog;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    user_id VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    hp INT NOT NULL DEFAULT 100,
    max_hp INT NOT NULL DEFAULT 100,
    exp INT NOT NULL DEFAULT 0,
    next_exp INT NOT NULL DEFAULT 100,
    level INT NOT NULL DEFAULT 1,
    gold INT NOT NULL DEFAULT 0
);

-- 사용자 상태 테이블
CREATE TABLE IF NOT EXISTS user_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    last_daily_reset DATE NOT NULL,
    daily_quests_completed INT NOT NULL DEFAULT 0,
    daily_login_count INT NOT NULL DEFAULT 0,
    boss_ready BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 인벤토리 테이블
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    potions INT NOT NULL DEFAULT 0,
    weapon_id VARCHAR(50),
    weapon_name VARCHAR(100),
    weapon_atk INT DEFAULT 0,
    armor_id VARCHAR(50),
    armor_name VARCHAR(100),
    armor_def INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 상점 아이템 테이블
CREATE TABLE IF NOT EXISTS shop_items (
    item_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price INT NOT NULL,
    type ENUM('WEAPON', 'ARMOR', 'POTION') NOT NULL,
    rarity ENUM('COMMON', 'RARE', 'EPIC', 'LEGENDARY') NOT NULL,
    stat_value INT NOT NULL,
    image_url VARCHAR(255)
);

-- 일정 테이블
CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    memo VARCHAR(500),
    category ENUM('STUDY', 'WORK', 'EXERCISE') NOT NULL,
    status ENUM('PENDING', 'DONE') NOT NULL DEFAULT 'PENDING',
    date DATE NOT NULL,
    due_time TIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
