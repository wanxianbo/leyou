package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_brand")
public class Brand {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;    //品牌id
    private String name;    //品牌名称
    private String image;   //品牌土拍你地址
    private String letter;  //品牌的首字母
}
