package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/brand")
public interface BrandAPI {
    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{bid}")
    Brand queryBrandById(@PathVariable("bid") Long id);

    /**
     * 根据ids查询多个品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    List<Brand> queryBrands(@RequestParam("ids") List<Long> ids);
}
