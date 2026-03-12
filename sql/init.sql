-- 1. 切换/创建数据库
CREATE DATABASE IF NOT EXISTS `insightguard` DEFAULT CHARACTER SET utf8mb4;
USE `insightguard`;

-- 2. 创建患者/家属表 (加上密码字段)
CREATE TABLE `patient_info` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `age` INT DEFAULT 0,
    `emergency_contact` VARCHAR(20),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建核心生理指标表
CREATE TABLE `health_records` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` BIGINT NOT NULL,
    `heart_rate` INT NOT NULL,
    `resp_rate` INT NOT NULL,
    `spo2` INT NOT NULL,
    `motion_scale` FLOAT DEFAULT 0.0,
    `status_code` VARCHAR(20) DEFAULT 'NORMAL',
    `stress_level` INT DEFAULT 1,
    `ai_insight` TEXT,
    `record_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_patient_time` (`patient_id`, `record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建ai建议及背景长期储存表

CREATE TABLE `ai_advice_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` BIGINT NOT NULL COMMENT '关联的患者ID',
  `heart_rate_avg` INT DEFAULT NULL COMMENT '该阶段的平均心率',
  `advice_content` TEXT NOT NULL COMMENT 'AI 生成的系统性建议',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建议生成时间',
  PRIMARY KEY (`id`),
  -- 建立索引，方便以后按患者 ID 快速查询历史建议
  INDEX `idx_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



