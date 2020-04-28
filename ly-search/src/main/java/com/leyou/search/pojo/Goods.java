package com.leyou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
@Document(indexName = "goods", type = "docs", shards = 1)
public class Goods {
    @Id
    private Long id;//spuId
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all;//所有需要搜索的信息，包括分类，品牌，价格，规格参数等
    @Field(type = FieldType.Keyword,index = false)
    private String subTile;//促销信息

    private Long brandId;//品牌id
    private Long cid1;//一级分类
    private Long cid2;//二级分类
    private Long cid3;//三级分类
    private Date createTime;//可根据产品的发布时间搜索
    private Set<Long> price;//商品价格，有多个，因此用集合

    @Field(type = FieldType.Keyword,index = false)
    private String skus;//sku信息的json结构,用于页面展示

    private Map<String,Object> specs;//可搜索的规格参数，key时参数名，Object是参数值
}
