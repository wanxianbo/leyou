package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("/spec")
public interface SpecificationAPI {
    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    List<SpecGroup> queryGroupByCid(@PathVariable("cid") Long cid);

    /**
     * 根据gid查询商品规格属性
     *
     * @param gid
     * @return
     */
    @GetMapping("/params")
    List<SpecParam> querySpecParams(
            @RequestParam(name = "gid",required = false) Long gid,
            @RequestParam(name = "cid",required = false) Long cid,
            @RequestParam(name = "searching",required = false) Boolean searching
    );
}
