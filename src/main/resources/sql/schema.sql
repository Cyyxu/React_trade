-- 如果数据库不存在则创建（MySQL 语句）
CREATE DATABASE IF NOT EXISTS offshore_wind_security
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

-- 使用数据库
USE offshore_wind_security;

SET FOREIGN_KEY_CHECKS = 1;