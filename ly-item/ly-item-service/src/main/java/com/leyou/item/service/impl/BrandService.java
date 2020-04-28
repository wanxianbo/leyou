package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService implements IBrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //分页
        PageHelper.startPage(page, rows);
        //模糊查询
        val example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        //排序
        example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(),list);
    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //插入数据到数据库
        int count = brandMapper.insert(brand);
        //对count进行校验
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_INTO_ERROR);
        }
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.INSERT_INTO_ERROR);
            }
        }
    }

    @Override
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        int count = brandMapper.updateByPrimaryKey(brand);
        //对count进行校验
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_ERROR);
        }
        count = brandMapper.deleteCategoryByBid(brand.getId());
        if (count == 0) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.UPDATE_ERROR);
            }
        }
    }

    @Override
    @Transactional
    public void deleteBrand(Long bid) {
        int count = brandMapper.deleteByPrimaryKey(bid);
        if (count == 0) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    @Override
    public Brand queryBrandById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    @Override
    public List<Brand> queryBrands(List<Long> ids) {
        //根据ids查询多个品牌
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

}
