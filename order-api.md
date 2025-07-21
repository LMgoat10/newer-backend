# 订单管理系统 API 文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: Bearer Token
- **Content-Type**: `application/json`

## 数据模型

### 订单状态
```typescript
enum OrderStatus {
  PAID = 'PAID',           // 已支付
  TICKETED = 'TICKETED',   // 已出票
  REFUND_REQUEST = 'REFUND_REQUEST', // 申请退款
  REFUNDED = 'REFUNDED',   // 已退款
  CANCELLED = 'CANCELLED'  // 已取消
}
```

### 支付方式
```typescript
enum PayMethod {
  BALANCE = 'BALANCE',     // 余额支付
  ALIPAY = 'ALIPAY',      // 支付宝
  WECHAT = 'WECHAT'       // 微信支付
}
```

### 订单对象
```typescript
interface TicketOrder {
  id: string                // 订单ID
  userId: number           // 用户ID
  attractionId: number     // 景点ID
  visitDate: string        // 游玩日期 (YYYY-MM-DD)
  quantity: number         // 门票数量
  unitPrice: number        // 单价
  totalAmount: number      // 总金额
  contactName?: string     // 联系人姓名
  contactPhone?: string    // 联系电话
  contactIdcard?: string   // 身份证号
  address?: string         // 收货地址
  status: OrderStatus      // 订单状态
  payMethod: PayMethod     // 支付方式
  payTime: string          // 支付时间
  payAmount: number        // 支付金额
  refundReason?: string    // 退款原因
  refundTime?: string      // 退款时间
  refundAmount?: number    // 退款金额
  createdAt: string        // 创建时间
  updatedAt: string        // 更新时间
  // 扩展字段
  attractionName?: string  // 景点名称
  attractionImage?: string // 景点图片
}
```

## API 接口

### 1. 创建订单

**POST** `/api/orders`

#### 请求参数
```json
{
  "attractionId": 1,
  "visitDate": "2025-08-15",
  "quantity": 2,
  "unitPrice": 100.00,
  "totalAmount": 200.00,
  "contactName": "张三",
  "contactPhone": "13800138000",
  "contactIdcard": "310101199001011234",
  "address": "上海市黄浦区南京路100号",
  "payMethod": "ALIPAY",
  "cartItemIds": ["cart_item_1", "cart_item_2"]
}
```

#### 响应
```json
{
  "success": true,
  "message": "订单创建成功",
  "data": {
    "orderId": "order_1234567890"
  }
}
```

#### 错误响应
```json
{
  "success": false,
  "message": "创建订单失败：库存不足",
  "code": "INSUFFICIENT_STOCK"
}
```

---

### 2. 获取用户订单列表

**GET** `/api/orders/user/{userId}`

#### 查询参数
- `pageNum`: 页码 (默认: 1)
- `pageSize`: 每页数量 (默认: 20)
- `status`: 订单状态 (可选)
- `startDate`: 开始日期 YYYY-MM-DD (可选)
- `endDate`: 结束日期 YYYY-MM-DD (可选)

#### 请求示例
```
GET /api/orders/user/123?pageNum=1&pageSize=10&status=PAID&startDate=2025-01-01&endDate=2025-12-31
```

#### 响应
```json
{
  "success": true,
  "message": "获取订单列表成功",
  "data": {
    "total": 100,
    "pageSize": 10,
    "pageNum": 1,
    "pages": 10,
    "orders": [
      {
        "id": "order_1234567890",
        "userId": 123,
        "attractionId": 1,
        "attractionName": "故宫博物院",
        "attractionImage": "https://example.com/image.jpg",
        "visitDate": "2025-08-15",
        "quantity": 2,
        "unitPrice": 100.00,
        "totalAmount": 200.00,
        "contactName": "张三",
        "contactPhone": "13800138000",
        "status": "PAID",
        "payMethod": "ALIPAY",
        "payTime": "2025-07-20T10:30:00Z",
        "payAmount": 200.00,
        "createdAt": "2025-07-20T10:00:00Z",
        "updatedAt": "2025-07-20T10:30:00Z"
      }
    ]
  }
}
```

---

### 3. 获取订单详情

**GET** `/api/orders/{orderId}`

#### 响应
```json
{
  "success": true,
  "message": "获取订单详情成功",
  "data": {
    "id": "order_1234567890",
    "userId": 123,
    "attractionId": 1,
    "attractionName": "故宫博物院",
    "attractionImage": "https://example.com/image.jpg",
    "visitDate": "2025-08-15",
    "quantity": 2,
    "unitPrice": 100.00,
    "totalAmount": 200.00,
    "contactName": "张三",
    "contactPhone": "13800138000",
    "contactIdcard": "310101199001011234",
    "address": "上海市黄浦区南京路100号",
    "status": "PAID",
    "payMethod": "ALIPAY",
    "payTime": "2025-07-20T10:30:00Z",
    "payAmount": 200.00,
    "createdAt": "2025-07-20T10:00:00Z",
    "updatedAt": "2025-07-20T10:30:00Z"
  }
}
```

---

### 4. 申请退款

**POST** `/api/orders/{orderId}/refund`

#### 请求参数
```json
{
  "refundReason": "行程取消"
}
```

#### 响应
```json
{
  "success": true,
  "message": "退款申请已提交，我们会在3-5个工作日内处理"
}
```

#### 业务规则
- 只有状态为 `PAID` 的订单可以申请退款
- 游玩日期必须在当前日期之后
- 退款金额为订单的实际支付金额

---

### 5. 取消订单

**PUT** `/api/orders/{orderId}/cancel`

#### 响应
```json
{
  "success": true,
  "message": "订单已取消"
}
```

#### 业务规则
- 只有状态为 `PAID` 的订单可以取消
- 取消后订单状态变为 `CANCELLED`
- 已支付的金额会原路退回

---

### 6. 获取订单统计

**GET** `/api/orders/user/{userId}/stats`

#### 响应
```json
{
  "success": true,
  "message": "获取统计信息成功",
  "data": {
    "totalOrders": 25,
    "paidOrders": 20,
    "ticketedOrders": 18,
    "refundedOrders": 2
  }
}
```

---

### 7. 重新支付订单

**POST** `/api/orders/{orderId}/retry-payment`

#### 响应
```json
{
  "success": true,
  "message": "支付链接已生成",
  "data": {
    "paymentUrl": "https://payment.example.com/pay?order=1234567890"
  }
}
```

#### 业务规则
- 只有支付失败的订单可以重新支付
- 重新支付时订单信息保持不变

---

## 错误码说明

| 错误码 | 描述 | HTTP状态码 |
|--------|------|-----------|
| SUCCESS | 操作成功 | 200 |
| INVALID_PARAM | 参数错误 | 400 |
| UNAUTHORIZED | 未授权 | 401 |
| ORDER_NOT_FOUND | 订单不存在 | 404 |
| INSUFFICIENT_STOCK | 库存不足 | 400 |
| ORDER_STATUS_ERROR | 订单状态错误 | 400 |
| PAYMENT_FAILED | 支付失败 | 400 |
| REFUND_NOT_ALLOWED | 不允许退款 | 400 |
| CANCEL_NOT_ALLOWED | 不允许取消 | 400 |
| INTERNAL_ERROR | 内部服务器错误 | 500 |

## 通用错误响应格式

```json
{
  "success": false,
  "message": "错误描述信息",
  "code": "ERROR_CODE",
  "timestamp": "2025-07-20T10:00:00Z"
}
```

## 认证

所有API都需要在请求头中包含认证token：

```
Authorization: Bearer {token}
```

## 数据库表结构参考

### ticket_order 表
```sql
CREATE TABLE ticket_order (
    id VARCHAR(50) PRIMARY KEY,
    user_id INT NOT NULL,
    attraction_id INT NOT NULL,
    visit_date DATE NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    contact_idcard VARCHAR(20),
    address TEXT,
    status VARCHAR(20) NOT NULL,
    pay_method VARCHAR(20) NOT NULL,
    pay_time DATETIME,
    pay_amount DECIMAL(10,2),
    refund_reason VARCHAR(500),
    refund_time DATETIME,
    refund_amount DECIMAL(10,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_visit_date (visit_date),
    INDEX idx_created_at (created_at)
);
```

## 业务流程

### 订单创建流程
1. 用户在购物车选择商品
2. 跳转到订单确认页面填写信息
3. 调用创建订单API
4. 后端验证库存和用户信息
5. 创建订单记录
6. 清理购物车对应商品
7. 返回订单ID

### 支付流程
1. 订单创建成功后状态为 `PAID`（模拟已支付）
2. 实际项目中会调用第三方支付接口
3. 支付成功后更新订单状态和支付信息

### 退款流程
1. 用户申请退款
2. 订单状态变更为 `REFUND_REQUEST`
3. 人工审核后处理退款
4. 退款成功后状态变更为 `REFUNDED`

### 出票流程
1. 支付成功的订单在游玩日期前自动出票
2. 订单状态变更为 `TICKETED`
3. 发送电子票据到用户邮箱或手机

## 注意事项

1. **时区处理**: 所有时间字段使用UTC时间，前端需要转换为本地时间显示
2. **金额精度**: 金额字段使用DECIMAL类型，保留2位小数
3. **并发控制**: 涉及库存扣减的操作需要使用数据库锁或Redis分布式锁
4. **数据一致性**: 订单创建和购物车清理需要在同一个事务中完成
5. **安全验证**: 所有订单操作都需要验证用户身份，确保用户只能操作自己的订单
