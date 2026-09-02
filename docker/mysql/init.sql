-- Zentra database initialization script
-- Schema and deterministic seed data for development and integration testing

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- Schema
-- =========================

DROP TABLE IF EXISTS `audit_log`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `dish`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `merchant`;

-- =========================
-- Merchant
-- =========================

CREATE TABLE `merchant` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                            `name` varchar(100) NOT NULL COMMENT 'Merchant name',
                            `status` tinyint DEFAULT '1' COMMENT 'Status: 1-active, 0-disabled',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =========================
-- Employee
-- =========================

CREATE TABLE `employee` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Employee ID',
                            `merchant_id` bigint NOT NULL COMMENT 'Merchant ID',
                            `username` varchar(50) NOT NULL COMMENT 'Login username',
                            `password` varchar(255) NOT NULL COMMENT 'Encrypted password (MD5)',
                            `name` varchar(100) DEFAULT NULL COMMENT 'Employee name',
                            `role` varchar(50) DEFAULT NULL COMMENT 'Employee role',
                            `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Employee status: 1-active, 0-disabled',
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Employee table';

-- =========================
-- Category
-- =========================

CREATE TABLE `category` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `merchant_id` bigint NOT NULL COMMENT 'Merchant ID',
                            `name` varchar(100) NOT NULL COMMENT 'Category name',
                            `description` varchar(255) DEFAULT NULL COMMENT 'Category description',
                            `sort` int DEFAULT '0' COMMENT 'Sort order',
                            `status` tinyint DEFAULT '1' COMMENT 'Status',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `type` int NOT NULL DEFAULT '1' COMMENT '1: dish, 2: setmeal',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_category_merchant_name` (`merchant_id`, `name`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =========================
-- Dish
-- =========================

CREATE TABLE `dish` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Dish ID',
                        `name` varchar(64) NOT NULL COMMENT 'Dish name',
                        `price` decimal(10,2) NOT NULL COMMENT 'Dish price',
                        `category_id` bigint NOT NULL COMMENT 'Category ID',
                        `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Dish status: 0 = Disabled, 1 = Enabled',
                        `merchant_id` bigint NOT NULL COMMENT 'Merchant ID',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_dish_merchant_name` (`merchant_id`, `name`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =========================
-- User
-- =========================

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `merchant_id` bigint NOT NULL COMMENT 'Merchant ID',
                        `username` varchar(50) NOT NULL COMMENT 'Username',
                        `password` varchar(100) NOT NULL COMMENT 'Password',
                        `nickname` varchar(50) DEFAULT NULL COMMENT 'Nickname',
                        `phone` varchar(20) DEFAULT NULL COMMENT 'Phone number',
                        `status` int NOT NULL DEFAULT '1' COMMENT '1=ACTIVE, 0=DISABLED',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =========================
-- Orders
-- =========================

CREATE TABLE `orders` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                          `merchant_id` bigint NOT NULL COMMENT 'Merchant ID (multi-tenant isolation)',
                          `user_id` bigint NOT NULL,
                          `order_number` varchar(64) NOT NULL COMMENT 'Business order number',
                          `total_amount` decimal(10,2) NOT NULL COMMENT 'Total order amount',
                          `status` int NOT NULL COMMENT 'Order status: 1-pending, 2-paid, 3-completed, 4-cancelled',
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                          `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          KEY `idx_merchant_id` (`merchant_id`),
                          KEY `idx_order_number` (`order_number`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Order main table';

-- =========================
-- Order Item
-- =========================

CREATE TABLE `order_item` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                              `order_id` bigint NOT NULL COMMENT 'Order ID',
                              `dish_id` bigint NOT NULL COMMENT 'Dish ID',
                              `dish_name` varchar(100) NOT NULL COMMENT 'Dish name snapshot',
                              `price` decimal(10,2) NOT NULL COMMENT 'Dish price snapshot',
                              `quantity` int NOT NULL COMMENT 'Quantity',
                              `amount` decimal(10,2) NOT NULL COMMENT 'Subtotal amount (price * quantity)',
                              `merchant_id` bigint NOT NULL COMMENT 'Merchant ID',
                              PRIMARY KEY (`id`),
                              KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Order item table';

-- =========================
-- Audit Log
-- =========================

CREATE TABLE `audit_log` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `operator_id` bigint NOT NULL COMMENT 'Operator ID',
                             `operator_role` varchar(64) NOT NULL COMMENT 'Operator role',
                             `operation` varchar(128) NOT NULL COMMENT 'Operation name',
                             `resource_type` varchar(64) NOT NULL COMMENT 'Business resource type',
                             `resource_id` bigint DEFAULT NULL COMMENT 'Business resource identifier',
                             `request_uri` varchar(255) NOT NULL COMMENT 'Request URI',
                             `request_method` varchar(16) NOT NULL COMMENT 'HTTP method',
                             `execution_time` bigint NOT NULL COMMENT 'Execution time in milliseconds',
                             `success` tinyint(1) NOT NULL COMMENT 'Operation result',
                             `error_message` varchar(1000) DEFAULT NULL COMMENT 'Failure reason',
                             `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- Deterministic Seed Data
-- =========================

-- Merchant

INSERT INTO `merchant`
(
    `id`,
    `name`,
    `status`
)
VALUES
    (
        1,
        'Demo Merchant',
        1
    );

-- Employees

INSERT INTO `employee`
(
    `id`,
    `merchant_id`,
    `username`,
    `password`,
    `name`,
    `role`,
    `status`
)
VALUES
    (
        1,
        1,
        'admin',
        '$2a$10$77jOG3Q2WsQ4DY6.FW5L7uDj9NOkcUTTCgG.HuIDavgcryP4ASkV6',
        'Admin User',
        'SUPER_ADMIN',
        1
    ),
    (
        2,
        1,
        'manager',
        '$2a$10$pQuUdSt.5eaRSWsb0UpGGupBIwKnGS6E7KnQ2H2l11tWLguxfSbUO',
        'Test User',
        'STORE_MANAGER',
        1
    ),
    (
        3,
        1,
        'cashier',
        '$2a$10$OZTq9Gz86XTnEGjmf.ec0OUFJZqOxO/wnqvkzFPiB.7NrvZLxYuca',
        'Cashier',
        'CASHIER',
        1
    ),
    (
        4,
        1,
        'kitchen',
        '$2a$10$10dbNRI3vumABDYFsZGAJOJj9EBYiQaTn6AuhI6g/AJUi4h5tdJAG',
        'Kitchen Staff',
        'KITCHEN_STAFF',
        1
    );

-- Category

INSERT INTO `category`
(
    `id`,
    `merchant_id`,
    `name`,
    `description`,
    `sort`,
    `status`,
    `type`
)
VALUES
    (
        2,
        1,
        'Hot Dishes',
        'For hot dishes',
        0,
        1,
        1
    );

-- Dishes

INSERT INTO `dish`
(
    `id`,
    `name`,
    `price`,
    `category_id`,
    `status`,
    `merchant_id`
)
VALUES
    (
        2,
        'GongBaoJiDing',
        18.80,
        2,
        1,
        1
    ),
    (
        3,
        'BaiCaiFenTiaoRou',
        21.50,
        2,
        1,
        1
    );

-- Customer User

INSERT INTO `user`
(
    `id`,
    `merchant_id`,
    `username`,
    `password`,
    `nickname`,
    `phone`,
    `status`
)
VALUES
    (
        2,
        1,
        'testUser',
        '$2a$10$nivbMlHQe2UmX5hAcfyTweHSdDSmdhSvDsxCB6c6vLgbBIuE7.ZKm',
        'TestNickname',
        '+77071234567',
        1
    );