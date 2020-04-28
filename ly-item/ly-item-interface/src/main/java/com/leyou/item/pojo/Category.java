package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_category")
public class Category {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;    //类名id
    private String name;    //类目名称
    private Long parentId;  //父类目id
    private Boolean isParent;   //是否为父节点
    private Integer sort;   //排序级数
}
