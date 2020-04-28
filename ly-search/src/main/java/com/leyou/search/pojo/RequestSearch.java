package com.leyou.search.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class RequestSearch {
    private String key;// 搜索页面的关键字
    private Integer page;
    private Integer size;
    private Map<String, String> filter;// 发送到后台的过滤项
    private Boolean descending;//是否降序

    private static final Integer DEFAULT_SIZE = 20;//默认每页大小
    private static final Integer DEFAULT_PAGE = 1;//默认当前页

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, this.page);
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
