-- 钱包系统相关数据库表结构（基于现有user表）
-- user 表已存在，只需要添加钱包相关的表

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(50) UNIQUE NOT NULL COMMENT '支付ID',
    order_id VARCHAR(50) NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method ENUM('BALANCE', 'ALIPAY', 'WECHAT') NOT NULL COMMENT '支付方式',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_id (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 充值记录表
CREATE TABLE IF NOT EXISTS recharge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recharge_id VARCHAR(50) UNIQUE NOT NULL COMMENT '充值ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    payment_method ENUM('ALIPAY', 'WECHAT') NOT NULL COMMENT '充值方式',
    status ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '充值状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_recharge_id (recharge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS refund (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_id VARCHAR(50) UNIQUE NOT NULL COMMENT '退款ID',
    order_id VARCHAR(50) NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    reason VARCHAR(500) COMMENT '退款原因',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') NOT NULL DEFAULT 'PENDING' COMMENT '退款状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    admin_remark VARCHAR(500) COMMENT '管理员备注',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_refund_id (refund_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 为测试目的，给现有用户设置初始余额
UPDATE user SET balance = 1000.00 WHERE user_id = 1;

-- 插入一些测试数据（如果需要的话）
-- 注意：这些插入语句只在对应记录不存在时执行

-- 测试用户（如果不存在）
INSERT IGNORE INTO user (user_id, name, email, phone, hashed_password, member_level, balance) 
VALUES (1, '测试用户', 'test@example.com', '13800138000', 'hashed_password_here', 'basic', 1000.00);

-- 可以添加更多测试数据...

-- 查看表结构
-- DESCRIBE payment;
-- DESCRIBE recharge;
-- DESCRIBE refund;
