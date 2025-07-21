package com.newer.jay.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;        // 当前页数据
    private int currentPage;        // 当前页码（从1开始）
    private int pageSize;           // 页面大小
    private long totalElements;     // 总记录数
    private int totalPages;         // 总页数
    private boolean first;          // 是否第一页
    private boolean last;           // 是否最后一页
    private boolean empty;          // 是否为空
    
    public PageResponse() {}
    
    public PageResponse(List<T> content, int currentPage, int pageSize, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.first = currentPage == 1;
        this.last = currentPage >= totalPages;
        this.empty = content == null || content.isEmpty();
    }
}
