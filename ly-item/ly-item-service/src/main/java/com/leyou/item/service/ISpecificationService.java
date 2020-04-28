package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface ISpecificationService {

    List<SpecGroup> queryGroupByCid(Long cid);

    List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic,Boolean searching);

    void saveSpecGroup(SpecGroup specGroup);

    void deleteSpecGroup(Long gid);

    void updateSpecGroup(SpecGroup specGroup);

    void saveSpecParams(SpecParam specParam);

    void deleteSpecParams(Long pid);

    void updateSpecParams(SpecParam specParam);

    List<SpecGroup> queryGroupAndParamByCid(Long cid);
}
