package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> queryCategoryListByPid(Long pid);

    List<Category> queryCategoryByBid(Long bid);

    List<Category> queryNameByIds(List<Long> ids);

    List<Category> queryAllByCid3(Long id);
}
