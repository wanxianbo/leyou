package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationService implements ISpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据cid查询商品规格组
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(group);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据gid查询商品规格属性
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAMS_NOT_FOUND);
        }
        return list;
    }

    /**
     * 添加新的商品规格组
     * @param specGroup
     */
    @Transactional
    @Override
    public void saveSpecGroup(SpecGroup specGroup) {
        //null值使用数据库的默认值
        int count = specGroupMapper.insertSelective(specGroup);
        if (count == 0) {
            throw new LyException(ExceptionEnum.INSERT_INTO_ERROR);
        }
    }

    /**
     * 根据主键gid删除商品规格组
     * @param gid
     */
    @Transactional
    @Override
    public void deleteSpecGroup(Long gid) {
        int count = specGroupMapper.deleteByPrimaryKey(gid);
        if (count == 0) {
            throw new LyException(ExceptionEnum.INSERT_INTO_ERROR);
        }
    }

    /**
     * 更新商品规格组
     * @param specGroup
     */
    @Transactional
    @Override
    public void updateSpecGroup(SpecGroup specGroup) {
        int count = specGroupMapper.updateByPrimaryKey(specGroup);
        if (count == 0) {
            throw new LyException(ExceptionEnum.UPDATE_ERROR);
        }
    }

    /**
     * 添加新的商品规格属性
     *
     * @param specParam
     */
    @Transactional
    @Override
    public void saveSpecParams(SpecParam specParam) {
        //null值使用数据库的默认值
        int count = specParamMapper.insertSelective(specParam);
        if (count == 0) {
            throw new LyException(ExceptionEnum.INSERT_INTO_ERROR);
        }
    }

    /**
     * 根据主键删除商品规格参数
     * @param pid
     */
    @Transactional
    @Override
    public void deleteSpecParams(Long pid) {
        int count = specParamMapper.deleteByPrimaryKey(pid);
        if (count == 0) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
    }

    /**
     * 更新商品规格参数
     * @param specParam
     */
    @Transactional
    @Override
    public void updateSpecParams(SpecParam specParam) {
        int count = specParamMapper.updateByPrimaryKey(specParam);
        if (count == 0) {
            throw new LyException(ExceptionEnum.UPDATE_ERROR);
        }
    }
}
