package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {


    @Autowired
    private ISpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        //http://api.leyou.com/api/item/spec/groups/79
        List<SpecGroup> list = specificationService.queryGroupByCid(cid);
        return ResponseEntity.ok(list);
    }

    /**
     * 查询规格参数组，及组内参数
     * @param cid
     * @return
     */
    @GetMapping("/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupAndParamByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryGroupAndParamByCid(cid));
    }
    /**
     * 根据gid查询商品规格属性
     *
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(name = "gid",required = false) Long gid,
            @RequestParam(name = "cid",required = false) Long cid,
            @RequestParam(name = "generic",required = false) Boolean generic,
            @RequestParam(name = "searching",required = false) Boolean searching
            ) {
        //http://api.leyou.com/api/item/spec/params?...
        List<SpecParam> list = specificationService.querySpecParams(gid,cid,generic,searching);
        return ResponseEntity.ok(list);
    }

    /**
     * 添加新的商品规格组
     *
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup) {
        //http://api.leyou.com/api/item/spec/group
        specificationService.saveSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据主键删除商品规格组
     *
     * @param gid
     * @return
     */
    @DeleteMapping("/group/{gid}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("gid") Long gid) {
        //http://api.leyou.com/api/item/spec/group/3
        specificationService.deleteSpecGroup(gid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新商品规格组
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup) {
        //http://api.leyou.com/api/item/spec/group
        specificationService.updateSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParams(@RequestBody SpecParam specParam) {
        //http://api.leyou.com/api/item/spec/param
        specificationService.saveSpecParams(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据主键删除商品规格参数
     *
     * @param pid
     * @return
     */
    @DeleteMapping("/param/{pid}")
    public ResponseEntity<Void> deleteSpecParams(@PathVariable("pid") Long pid) {
        //http://api.leyou.com/api/item/spec/param/3
        specificationService.deleteSpecParams(pid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新商品规格属性
     * @param specParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParams(@RequestBody SpecParam specParam) {
        //http://api.leyou.com/api/item/spec/param
        specificationService.updateSpecParams(specParam);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}