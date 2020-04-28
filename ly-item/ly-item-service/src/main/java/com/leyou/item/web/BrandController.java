package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private IBrandService brandService;

    /**
     * 分页查询品牌
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(name = "key",required = false) String key,//搜索条件
            @RequestParam(name = "page",defaultValue = "1") Integer page,//当前页
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,//每页条数
            @RequestParam(name = "sortBy",defaultValue = "id") String sortBy,//排序字段
            @RequestParam(name = "desc",defaultValue = "false") Boolean desc//是否降序
    ) {
        PageResult<Brand> pageResult = brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{bid}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("bid") Long id) {
        return ResponseEntity.ok(brandService.queryBrandById(id));
    }

    /**
     * 根据ids查询多个品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrands(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrands(ids));
    }
    /**
     * 根据cid查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    //http://api.leyou.com/api/item/brand
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新品牌
     * @param brand
     * @param cids
     * @return
     */
    //http://api.leyou.com/api/item/brand
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据主键删除品牌
     * @param bid
     * @return
     */
    //http://api.leyou.com/api/item/brand/325403
    @DeleteMapping("{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid) {
        brandService.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
