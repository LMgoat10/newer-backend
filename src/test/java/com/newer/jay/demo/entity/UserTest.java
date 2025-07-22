package com.newer.jay.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * User实体类测试 - 基于JUnit5
 */
@DisplayName("User实体类测试")
public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("测试User构造函数")
    void testUserConstructors() {
        // 测试无参构造函数
        User user1 = new User();
        assertNotNull(user1);

        // 测试全参构造函数
        Date joinDate = new Date();
        User user2 = new User(1L, "张三", "zhangsan@example.com", "13800138000", 
                             "hashedPassword", "avatar.jpg", "VIP", joinDate, 
                             10, 500, 1000.0);
        
        assertEquals(Long.valueOf(1L), user2.getUserId());
        assertEquals("张三", user2.getName());
        assertEquals("zhangsan@example.com", user2.getEmail());
        assertEquals("13800138000", user2.getPhone());
        assertEquals("hashedPassword", user2.getHashedPassword());
        assertEquals("avatar.jpg", user2.getAvatarFileName());
        assertEquals("VIP", user2.getMemberLevel());
        assertEquals(joinDate, user2.getJoinDate());
        assertEquals(Integer.valueOf(10), user2.getTotalOrders());
        assertEquals(Integer.valueOf(500), user2.getPoints());
        assertEquals(Double.valueOf(1000.0), user2.getBalance());
    }

    @Test
    @DisplayName("测试User的Setter和Getter方法")
    void testUserSettersAndGetters() {
        // 测试基本字段的setter和getter
        user.setUserId(1L);
        user.setName("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setHashedPassword("hashedPassword123");
        user.setAvatarFileName("test-avatar.jpg");
        user.setMemberLevel("Gold");
        user.setTotalOrders(5);
        user.setPoints(250);
        user.setBalance(500.0);
        
        Date testDate = new Date();
        user.setJoinDate(testDate);

        // 验证所有字段
        assertEquals(Long.valueOf(1L), user.getUserId());
        assertEquals("测试用户", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("13800138000", user.getPhone());
        assertEquals("hashedPassword123", user.getHashedPassword());
        assertEquals("test-avatar.jpg", user.getAvatarFileName());
        assertEquals("Gold", user.getMemberLevel());
        assertEquals(testDate, user.getJoinDate());
        assertEquals(Integer.valueOf(5), user.getTotalOrders());
        assertEquals(Integer.valueOf(250), user.getPoints());
        assertEquals(Double.valueOf(500.0), user.getBalance());
    }

    @Test
    @DisplayName("测试User的toString方法")
    void testUserToString() {
        user.setUserId(1L);
        user.setName("测试用户");
        user.setEmail("test@example.com");
        
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("测试用户"));
        assertTrue(userString.contains("test@example.com"));
    }

    @Test
    @DisplayName("测试User的equals和hashCode方法")
    void testUserEqualsAndHashCode() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setName("测试用户");
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setUserId(1L);
        user2.setName("测试用户");
        user2.setEmail("test@example.com");

        // 测试equals方法
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        // 修改一个字段后应该不相等
        user2.setName("其他用户");
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("测试User.Preferences内部类")
    void testUserPreferences() {
        // 测试内部静态类Preferences
        User.Preferences preferences = new User.Preferences();
        assertNotNull(preferences);
        
        preferences.setLanguage("zh-CN");
        preferences.setCurrency("CNY");
        
        assertEquals("zh-CN", preferences.getLanguage());
        assertEquals("CNY", preferences.getCurrency());
    }

    @Test
    @DisplayName("测试User边界值")
    void testUserBoundaryValues() {
        // 测试null值
        user.setName(null);
        user.setEmail(null);
        assertNull(user.getName());
        assertNull(user.getEmail());

        // 测试空字符串
        user.setName("");
        user.setEmail("");
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());

        // 测试数值边界
        user.setBalance(0.0);
        user.setPoints(0);
        user.setTotalOrders(0);
        assertEquals(Double.valueOf(0.0), user.getBalance());
        assertEquals(Integer.valueOf(0), user.getPoints());
        assertEquals(Integer.valueOf(0), user.getTotalOrders());
    }
}
