package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("/category")
public interface CategoryAPI {
    @GetMapping("/names")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
}
