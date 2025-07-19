-- 钱包系统相关数据库表结构

-- 1. 钱包表
CREATE TABLE IF NOT EXISTS `wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';

-- 2. 支付记录表
CREATE TABLE IF NOT EXISTS `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_id` varchar(50) NOT NULL COMMENT '支付ID',
  `order_id` varchar(50) NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` enum('BALANCE','ALIPAY','WECHAT') NOT NULL COMMENT '支付方式',
  `status` enum('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_id` (`payment_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 3. 充值记录表
CREATE TABLE IF NOT EXISTS `recharge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `recharge_id` varchar(50) NOT NULL COMMENT '充值ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `payment_method` enum('BALANCE','ALIPAY','WECHAT') NOT NULL COMMENT '支付方式',
  `status` enum('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '充值状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recharge_id` (`recharge_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- 4. 退款记录表
CREATE TABLE IF NOT EXISTS `refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `refund_id` varchar(50) NOT NULL COMMENT '退款ID',
  `order_id` varchar(50) NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `status` enum('PENDING','APPROVED','REJECTED','COMPLETED') NOT NULL DEFAULT 'PENDING' COMMENT '退款状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `processed_at` datetime DEFAULT NULL COMMENT '处理时间',
  `admin_remark` varchar(500) DEFAULT NULL COMMENT '管理员备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_id` (`refund_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';

-- 5. 插入测试数据
-- 为用户ID=1创建钱包并设置初始余额
INSERT INTO `wallet` (`user_id`, `balance`) VALUES (1, 1000.00) 
ON DUPLICATE KEY UPDATE `balance` = VALUES(`balance`);

-- 插入一些测试充值记录
INSERT INTO `recharge` (`recharge_id`, `user_id`, `amount`, `payment_method`, `status`) VALUES 
('RECHARGE_20250719_001', 1, 500.00, 'ALIPAY', 'SUCCESS'),
('RECHARGE_20250719_002', 1, 300.00, 'WECHAT', 'SUCCESS');
