package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 分页查询商品分类
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam Long pid) {
        List<Category> categoryList = categoryService.queryCategoryListByPid(pid);
        return ResponseEntity.ok(categoryList);
    }

    /**
     * 根据bid查询分类
     * @param bid
     * @return
     */
    //http://api.leyou.com/api/item/category/bid/1115
    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryById(@PathVariable("bid") Long bid) {
        List<Category> list  = categoryService.queryCategoryByBid(bid);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据ids查询三级分类
     * @param ids
     * @return
     */
    @GetMapping("/names")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(categoryService.queryNameByIds(ids));
    }

    /**
     * 根据cid3查询3个类名
     * @param id
     * @return
     */
    @GetMapping("/all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id) {
        return ResponseEntity.ok(categoryService.queryAllByCid3(id));
    }

}
