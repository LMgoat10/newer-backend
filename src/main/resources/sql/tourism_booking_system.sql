-- 旅游预订系统数据库表结构

-- 1. 景点表（Attraction）
CREATE TABLE IF NOT EXISTS `attraction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name` varchar(100) NOT NULL COMMENT '景点名称',
  `description` text COMMENT '景点描述',
  `location` varchar(255) COMMENT '景点位置坐标',
  `address` varchar(255) COMMENT '详细地址',
  `city_name` varchar(50) COMMENT '城市名称',
  `province_name` varchar(50) COMMENT '省份名称',
  `area_name` varchar(50) COMMENT '区域名称',
  `price` decimal(10,2) NOT NULL COMMENT '基础门票价格（成人票）',
  `total_tickets` int NOT NULL DEFAULT 0 COMMENT '总票数',你
  `available_tickets` int NOT NULL DEFAULT 0 COMMENT '可用票数',
  `open_time` time COMMENT '开放时间',
  `close_time` time COMMENT '关闭时间',
  `open_time_str` varchar(100) COMMENT '开放时间文本描述',
  `rating` decimal(3,2) DEFAULT 4.50 COMMENT '评分',
  `review_count` int DEFAULT 0 COMMENT '评价数量',
  `phone` varchar(20) COMMENT '联系电话',
  `website` varchar(255) COMMENT '官方网站',
  `pic_list` text COMMENT '图片列表JSON',
  `tags` varchar(500) COMMENT '标签JSON',
  `status` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_city` (`city_name`),
  INDEX `idx_province` (`province_name`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点信息表';

-- 2. 购物车表（Cart Item）
CREATE TABLE IF NOT EXISTS `cart_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `attraction_id` bigint NOT NULL COMMENT '景点ID',
  `ticket_type` varchar(50) NOT NULL COMMENT '门票类型（adult/student/child等）',
  `ticket_price` decimal(10,2) NOT NULL COMMENT '门票价格',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '购买数量',
  `visit_date` date NOT NULL COMMENT '游玩日期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入购物车时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_attraction_id` (`attraction_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`attraction_id`) REFERENCES `attraction`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 3. 订单表（Ticket Order）
CREATE TABLE `ticket_order` (
    `id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `attraction_id` bigint NOT NULL COMMENT '景点ID',
    `visit_date` date NOT NULL COMMENT '游玩日期',
    `quantity` int NOT NULL DEFAULT '1' COMMENT '购票数量',
    `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
    `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
    `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人电话',
    `contact_idcard` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人身份证号',
    `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货地址',
    `status` enum(
        'PAID',
        'TICKETED',
        'REFUND_REQUEST',
        'REFUNDED',
        'CANCELLED'
    ) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PAID' COMMENT '订单状态',
    `pay_method` enum('BALANCE', 'ALIPAY', 'WECHAT') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付方式',
    `pay_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    `pay_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
    `refund_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款原因',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `refund_amount` decimal(10, 2) DEFAULT NULL COMMENT '退款金额',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_attraction_id` (`attraction_id`),
    KEY `idx_status` (`status`),
    KEY `idx_visit_date` (`visit_date`),
    KEY `idx_pay_time` (`pay_time`),
    CONSTRAINT `ticket_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `ticket_order_ibfk_2` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '门票订单表'

-- 4. 插入测试景点数据
INSERT INTO `attraction` (
  `name`, `description`, `address`, `city_name`, `province_name`, `area_name`, 
  `price`, `total_tickets`, `available_tickets`, `open_time`, `close_time`, `open_time_str`,
  `rating`, `review_count`, `phone`, `website`, 
  `pic_list`, `tags`, `status`
) VALUES 
('北京故宫', 
 '明清两代的皇家宫殿，中国古代宫廷建筑之精华', 
 '北京市东城区景山前街4号', '北京', '北京市', '东城区',
 60.00, 5000, 4500, '08:30:00', '17:00:00', '08:30-17:00',
 4.80, 12580, '010-85007421', 'https://www.dpm.org.cn',
 '["https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400&h=200&fit=crop"]',
 '["历史文化", "世界遗产", "皇家建筑"]', 'ACTIVE'),

('西安兵马俑', 
 '秦始皇兵马俑博物馆，世界文化遗产', 
 '西安市临潼区秦陵北路', '西安', '陕西省', '临潼区',
 120.00, 3000, 2800, '08:30:00', '18:00:00', '08:30-18:00',
 4.70, 8960, '029-81399001', 'http://www.bmy.com.cn',
 '["https://images.unsplash.com/photo-1549693578-d683be217e58?w=400&h=200&fit=crop"]',
 '["历史文化", "世界遗产", "考古"]', 'ACTIVE'),

('杭州西湖', 
 '人间天堂，UNESCO世界文化遗产', 
 '浙江省杭州市西湖区', '杭州', '浙江省', '西湖区',
 0.00, 10000, 9500, '00:00:00', '23:59:59', '全天开放',
 4.70, 8960, '0571-87977767', 'http://www.westlake.com.cn',
 '["https://images.unsplash.com/photo-1549693578-d683be217e58?w=400&h=200&fit=crop"]',
 '["自然风光", "古典园林", "文化名胜"]', 'ACTIVE'),

('上海外滩', 
 '万国建筑博览群，上海标志性景点', 
 '上海市黄浦区中山东一路', '上海', '上海市', '黄浦区',
 0.00, 8000, 7500, '00:00:00', '23:59:59', '全天开放',
 4.60, 15200, '021-63293888', 'http://www.thebund.com.cn',
 '["https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=200&fit=crop"]',
 '["城市风光", "建筑艺术", "夜景"]', 'ACTIVE'),

('桂林漓江', 
 '桂林山水甲天下，国家5A级旅游景区', 
 '广西壮族自治区桂林市', '桂林', '广西壮族自治区', '市区',
 210.00, 2000, 1800, '07:30:00', '18:30:00', '07:30-18:30',
 4.80, 6750, '0773-2825555', 'http://www.lijiang.com.cn',
 '["https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=200&fit=crop"]',
 '["自然风光", "山水", "国家5A"]', 'ACTIVE');

-- 5. 插入测试购物车数据（假设用户ID=1，对应现有用户）
INSERT INTO `cart_item` (`user_id`, `attraction_id`, `ticket_type`, `ticket_price`, `quantity`, `visit_date`) VALUES 
(1, 1, 'adult', 60.00, 2, '2025-08-15'),
(1, 2, 'adult', 120.00, 1, '2025-08-20'),
(1, 1, 'student', 30.00, 1, '2025-08-15');

-- 6. 插入测试订单数据
INSERT INTO `ticket_order` (
  `id`, `user_id`, `attraction_id`, `visit_date`, `quantity`, `unit_price`, `total_amount`,
  `contact_name`, `contact_phone`, `contact_idcard`, `address`,
  `status`, `pay_method`, `pay_time`, `pay_amount`
) VALUES 
('ORDER_20250720_001', 1, 1, '2025-08-15', 2, 60.00, 120.00,
 '张三', '13812345678', '110101199001011234', '北京市朝阳区xxx街道xxx号',
 'PAID', 'BALANCE', '2025-07-20 10:30:00', 120.00),
 
('ORDER_20250720_002', 1, 2, '2025-08-20', 1, 120.00, 120.00,
 '李四', '13987654321', '310101199001011234', '上海市浦东新区xxx路xxx号',
 'TICKETED', 'ALIPAY', '2025-07-20 14:20:00', 120.00),

('ORDER_20250720_004', 1, 5, '2025-08-25', 2, 210.00, 420.00,
 '王五', '13611223344', '440101199001011234', '广东省广州市天河区xxx街道xxx号',
 'REFUND_REQUEST', 'WECHAT', '2025-07-20 16:45:00', 420.00);

-- 7. 为测试退票申请设置退票原因
UPDATE `ticket_order` SET `refund_reason` = '因个人原因无法按时出行，申请退票退款' WHERE `id` = 'ORDER_20250720_004';
