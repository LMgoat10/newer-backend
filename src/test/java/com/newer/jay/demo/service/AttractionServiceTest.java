package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AttractionRequestDTO;
import com.newer.jay.demo.dto.AttractionResponseDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.mapper.AttractionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AttractionService服务类测试 - 基于JUnit5和Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AttractionService服务测试")
public class AttractionServiceTest {

    @Mock
    private AttractionMapper attractionMapper;

    @InjectMocks
    private AttractionService attractionService;

    private Attraction mockAttraction;
    private AttractionRequestDTO mockRequestDTO;

    @BeforeEach
    void setUp() {
        // 创建模拟的Attraction对象
        mockAttraction = new Attraction();
        mockAttraction.setId(1L);
        mockAttraction.setName("测试景点");
        mockAttraction.setDescription("这是一个测试景点");
        mockAttraction.setLocation("北京市");
        mockAttraction.setAddress("北京市朝阳区测试路123号");
        mockAttraction.setCityName("北京");
        mockAttraction.setProvinceName("北京市");
        mockAttraction.setPrice(new BigDecimal("88.00"));
        mockAttraction.setTotalTickets(1000);
        mockAttraction.setAvailableTickets(800);
        mockAttraction.setOpenTime(LocalTime.of(8, 0));
        mockAttraction.setCloseTime(LocalTime.of(18, 0));
        mockAttraction.setRating(new BigDecimal("4.5"));
        mockAttraction.setReviewCount(200);
        mockAttraction.setPhone("010-12345678");
        mockAttraction.setWebsite("http://test.com");
        mockAttraction.setPicList("[\"pic1.jpg\", \"pic2.jpg\"]");
        mockAttraction.setTags("[\"历史\", \"文化\"]");
        mockAttraction.setStatus(Attraction.AttractionStatus.ACTIVE);
        mockAttraction.setCreatedAt(LocalDateTime.now());
        mockAttraction.setUpdatedAt(LocalDateTime.now());

        // 创建模拟的RequestDTO
        mockRequestDTO = new AttractionRequestDTO();
        mockRequestDTO.setName("新景点");
        mockRequestDTO.setDescription("新景点描述");
        mockRequestDTO.setLocation("上海市");
        mockRequestDTO.setPrice(new BigDecimal("120.00"));
        mockRequestDTO.setTotalTickets(500);
        mockRequestDTO.setAvailableTickets(500);
    }

    @Test
    @DisplayName("测试获取景点列表 - 无筛选条件")
    void testGetAttractionListWithoutFilter() {
        // 准备测试数据
        List<Attraction> mockAttractions = Arrays.asList(mockAttraction);
        Page<Attraction> mockPage = new Page<>(1, 10);
        mockPage.setRecords(mockAttractions);
        mockPage.setTotal(1);

        // 模拟mapper行为
        when(attractionMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        Page<AttractionResponseDTO> result = attractionService.getAttractionList(1, 10, null, null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        
        AttractionResponseDTO dto = result.getRecords().get(0);
        assertEquals("测试景点", dto.getName());
        assertNotNull(dto.getId());

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("测试获取景点列表 - 带关键词筛选")
    void testGetAttractionListWithKeyword() {
        // 准备测试数据
        List<Attraction> mockAttractions = Arrays.asList(mockAttraction);
        Page<Attraction> mockPage = new Page<>(1, 10);
        mockPage.setRecords(mockAttractions);
        mockPage.setTotal(1);

        // 模拟mapper行为
        when(attractionMapper.selectPage(any(), any())).thenReturn(mockPage);

        // 执行测试
        Page<AttractionResponseDTO> result = attractionService.getAttractionList(1, 10, "测试", null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证mapper调用
        verify(attractionMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("测试获取景点列表 - 带城市筛选")
    void testGetAttractionListWithCity() {
        // 准备测试数据
        List<Attraction> mockAttractions = Arrays.asList(mockAttraction);
        Page<Attraction> mockPage = new Page<>(1, 10);
        mockPage.setRecords(mockAttractions);
        mockPage.setTotal(1);

        // 模拟mapper行为
        when(attractionMapper.selectPage(any(), any())).thenReturn(mockPage);

        // 执行测试
        Page<AttractionResponseDTO> result = attractionService.getAttractionList(1, 10, null, "北京", null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证mapper调用
        verify(attractionMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("测试获取景点详情 - 成功")
    void testGetAttractionByIdSuccess() {
        // 模拟mapper行为
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);

        // 执行测试
        AttractionResponseDTO result = attractionService.getAttractionById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("测试景点", result.getName());
        assertNotNull(result.getId());
        assertEquals("北京", result.getCityName());

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试获取景点详情 - 景点不存在")
    void testGetAttractionByIdNotFound() {
        // 模拟mapper行为
        when(attractionMapper.selectById(999L)).thenReturn(null);

        // 执行测试
        AttractionResponseDTO result = attractionService.getAttractionById(999L);

        // 验证结果
        assertNull(result);

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("测试创建景点 - 成功")
    void testCreateAttractionSuccess() {
        // 创建一个新的Attraction对象用于验证
        Attraction newAttraction = new Attraction();
        newAttraction.setId(2L);
        newAttraction.setName("新景点");
        newAttraction.setDescription("新景点描述");
        
        // 模拟mapper行为
        when(attractionMapper.insert(any(Attraction.class))).thenAnswer(invocation -> {
            Attraction attraction = invocation.getArgument(0);
            attraction.setId(2L); // 模拟数据库自增ID
            return 1;
        });

        // 执行测试
        AttractionResponseDTO result = attractionService.createAttraction(mockRequestDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("新景点", result.getName());

        // 验证mapper调用
        verify(attractionMapper, times(1)).insert(any(Attraction.class));
    }

    @Test
    @DisplayName("测试更新景点 - 成功")
    void testUpdateAttractionSuccess() {
        // 模拟mapper行为
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);
        when(attractionMapper.updateById(any(Attraction.class))).thenReturn(1);

        // 执行测试
        AttractionResponseDTO result = attractionService.updateAttraction(1L, mockRequestDTO);

        // 验证结果
        assertNotNull(result);

        // 验证mapper调用
        verify(attractionMapper, times(2)).selectById(1L); // 一次查询存在性，一次获取更新后结果
        verify(attractionMapper, times(1)).updateById(any(Attraction.class));
    }

    @Test
    @DisplayName("测试更新景点 - 景点不存在")
    void testUpdateAttractionNotFound() {
        // 模拟mapper行为
        when(attractionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attractionService.updateAttraction(999L, mockRequestDTO);
        });

        assertTrue(exception.getMessage().contains("景点不存在"));

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectById(999L);
        verify(attractionMapper, never()).updateById(any(Attraction.class));
    }

    @Test
    @DisplayName("测试删除景点 - 成功")
    void testDeleteAttractionSuccess() {
        // 模拟mapper行为
        when(attractionMapper.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = attractionService.deleteAttraction(1L);

        // 验证结果
        assertTrue(result);

        // 验证mapper调用
        verify(attractionMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除景点 - 景点不存在")
    void testDeleteAttractionNotFound() {
        // 模拟mapper行为 - 删除失败返回0
        when(attractionMapper.deleteById(999L)).thenReturn(0);

        // 执行测试
        boolean result = attractionService.deleteAttraction(999L);

        // 验证结果
        assertFalse(result);

        // 验证mapper调用
        verify(attractionMapper, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("测试获取空列表")
    void testGetEmptyAttractionList() {
        // 准备测试数据
        List<Attraction> emptyList = new ArrayList<>();
        Page<Attraction> mockPage = new Page<>(1, 10);
        mockPage.setRecords(emptyList);
        mockPage.setTotal(0);

        // 模拟mapper行为
        when(attractionMapper.selectPage(any(), any())).thenReturn(mockPage);

        // 执行测试
        Page<AttractionResponseDTO> result = attractionService.getAttractionList(1, 10, null, null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectPage(any(), any());
    }

    @Test
    @DisplayName("测试异常处理")
    void testExceptionHandling() {
        // 模拟mapper抛出异常
        when(attractionMapper.selectById(any())).thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试并验证异常处理
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attractionService.getAttractionById(1L);
        });

        assertTrue(exception.getMessage().contains("获取景点详情失败"));

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试创建景点 - 失败")
    void testCreateAttractionFailure() {
        // 模拟mapper插入失败
        when(attractionMapper.insert(any(Attraction.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attractionService.createAttraction(mockRequestDTO);
        });

        assertTrue(exception.getMessage().contains("景点创建失败"));

        // 验证mapper调用
        verify(attractionMapper, times(1)).insert(any(Attraction.class));
    }

    @Test
    @DisplayName("测试更新景点 - 失败")
    void testUpdateAttractionFailure() {
        // 模拟查询成功但更新失败
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);
        when(attractionMapper.updateById(any(Attraction.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attractionService.updateAttraction(1L, mockRequestDTO);
        });

        assertTrue(exception.getMessage().contains("景点更新失败"));

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectById(1L);
        verify(attractionMapper, times(1)).updateById(any(Attraction.class));
    }

    @Test
    @DisplayName("测试获取景点列表 - 分页参数验证")
    void testGetAttractionListPagination() {
        // 准备测试数据
        List<Attraction> mockAttractions = Arrays.asList(mockAttraction);
        Page<Attraction> mockPage = new Page<>(2, 5);
        mockPage.setRecords(mockAttractions);
        mockPage.setTotal(10);

        // 模拟mapper行为
        when(attractionMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        Page<AttractionResponseDTO> result = attractionService.getAttractionList(2, 5, null, null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(10, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(2, result.getCurrent());
        assertEquals(5, result.getSize());

        // 验证mapper调用
        verify(attractionMapper, times(1)).selectPage(any(), any());
    }
}
