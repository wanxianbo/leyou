package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryCategoryListByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        return list;
    }

    @Override
    public List<Category> queryCategoryByBid(Long bid) {
        List<Category> list = categoryMapper.queryCategoryByBid(bid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.ITEM_CATEGORY_NOT_FOUND);
        }
        return list;
    }

    /**
     * 通过ids查找cnames
     * @param ids
     * @return
     */
    @Override
    public List<Category> queryNameByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.ITEM_CATEGORY_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        List<Category> categoryList = Arrays.asList(c1, c2, c3);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.ITEM_CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }
}
