package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.RequestSearch;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;

    public Goods goodsBuilder(Spu spu) {
        //定义spuId
        Long spuId = spu.getId();
        //查询到分类对象
        List<Category> categoryList = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.ITEM_CATEGORY_NOT_FOUND);
        }
        //转成分类名称
        List<String> categoryNames = categoryList.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        String all = spu.getTitle() + StringUtils.join(categoryNames, " ") + brand.getName();
        //查询sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        //创建map用来装需要的sku属性
        Set<Long> prices = new HashSet<>();//价格集合
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            //处理价格
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("images", StringUtils.substringBefore(sku.getImages(),","));
            skuMap.put("price", sku.getPrice());
            skuList.add(skuMap);
        });

        //查询规格参数名
        List<SpecParam> params = specificationClient.querySpecParams(null, spu.getCid3(), true,null);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAMS_NOT_FOUND);
        }

        //查询规格参数值
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_DETAIL_NOT_FOUND);
        }
        //1.通用属性值
        Map<Long, String> genericSpec = JsonUtils.parseMap(
                spuDetail.getGenericSpec(), Long.class, String.class);
        //2.特有属性值
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(
                spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        //3.封装到map中
        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            String key = param.getName();
            Object value = "";
            //判断是否为通用属性
            if (param.getGeneric()) {
                value = genericSpec.get(param.getId());
                //判断是否为数字类型
                if (param.getNumeric()) {
                    //处理成段，此处是为了方便查询和提高查询速率
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                value = specialSpec.get(param.getId());
            }
            specs.put(key, value);
        });


        Goods goods = new Goods();
        //设置id
        goods.setId(spuId);
        //设置all
        goods.setAll(all);
        //设置促销信息
        goods.setSubTile(spu.getSubTitle());
        //设置品牌id
        goods.setBrandId(spu.getBrandId());
        //设置分类Id
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        //设置产品的发布时间
        goods.setCreateTime(spu.getCreateTime());
        //设置商品价格
        goods.setPrice(prices);
        //设置sku的title，price，images json
        goods.setSkus(JsonUtils.serialize(skuList));
        //设置参数名和参数值
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 对数字参数分段
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> searchGoods(RequestSearch requestSearch) {
        //获取当前页
        int page = requestSearch.getPage() - 1;
        //获取每页大小
        int size = requestSearch.getSize();
        //1.构建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //创建查询条件
        //QueryBuilder boolQueryBuilder = QueryBuilders.matchQuery("all", requestSearch.getKey()).operator(Operator.AND);
        QueryBuilder boolQueryBuilder = buildBoolQueryBuilder(requestSearch);
        //2.分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        //3.添加聚合
        //聚合名称
        String categoryAggName = "category_agg";
        String brandAggName = "brand_agg";
        //聚合分类
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //结果过滤，只要id，title，skus
        queryBuilder.withSourceFilter(
                new FetchSourceFilter(new String[]{"id", "title", "skus","subTile"}, null));
        //搜索过滤
        queryBuilder.withQuery(boolQueryBuilder);
        //查询
        //Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());
        AggregatedPage<Goods> aggregatedPage = template.queryForPage(queryBuilder.build(), Goods.class);
        if (aggregatedPage == null) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        //得到总页数
        int totalPage = aggregatedPage.getTotalPages();
        //得到总条数
        long total = aggregatedPage.getTotalElements();
        //得到数据
        List<Goods> goodsList = aggregatedPage.getContent();
        //解析聚合结果
        Aggregations aggs = aggregatedPage.getAggregations();
        // 分类聚合
        List<Map<String,Object>> categories = parseCategoryAgg(aggs.get(categoryAggName));
        // 品牌聚合
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));
        //根据商品分类个数判断是否需要聚合
        List<Map<String,Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            // 如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = parseParamAgg((Long) categories.get(0).get("cid"),boolQueryBuilder);
        }
        return new SearchResult(total,totalPage,goodsList,categories,brands,specs);
    }

    private QueryBuilder buildBoolQueryBuilder(RequestSearch requestSearch) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", requestSearch.getKey()));
        //添加过滤条件
        Map<String, String> filter = requestSearch.getFilter();
        if (CollectionUtils.isEmpty(filter)) {
            //过滤条件为空，直接返回基本查询条件
            return boolQueryBuilder;
        }
        //进行过滤
        filter.forEach((key, value) -> {
            if (StringUtils.equals("品牌", key)) {
                // 如果过滤条件是“品牌”, 过滤的字段名：brandId
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            }else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,value));
        });
        return boolQueryBuilder;
    }

    private List<Map<String,Object>> parseParamAgg(Long cid, QueryBuilder boolQueryBuilder) {
        try {
            List<Map<String, Object>> specs = new ArrayList<>();
            //1.创建自定义查询构建器
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            //2.基于基本查询条件，聚合规格参数
            queryBuilder.withQuery(boolQueryBuilder);
            //3.查询规格参数
            List<SpecParam> params = specificationClient.querySpecParams(null, cid, true,null);
            //4.添加聚合
            params.forEach(param -> {
                String name = param.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
            });
            //5.执行查询
            AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
            //6.获取聚合结果
            Aggregations aggs = result.getAggregations();
            //7.解析聚合结果
            params.forEach(param -> {
                Map<String, Object> map = new HashMap<>();
                String name = param.getName();
                StringTerms terms = aggs.get(name);
                map.put("k", name);
                map.put("options", terms.getBuckets().stream().
                        map(bucket -> bucket.getKeyAsString()).
                        collect(Collectors.toList()));
                specs.add(map);
            });
            return specs;
        } catch (Exception e) {
            log.error("规格聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 解析brandAggregate
     * @param terms
     * @return
     */
    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            //对桶进行遍历得到brandId
            List<Long> ids = terms.getBuckets().stream().
                    map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrands(ids);
            return brands;
        } catch (Exception e) {
            log.error("[品牌服务异常]",e.getMessage());
            return null;
        }
    }

    /**
     * 解析categoriesAggregate
     * @param terms
     * @return
     */
    private List<Map<String, Object>> parseCategoryAgg(LongTerms terms) {
        try {
            //对桶进行遍历得到cids
            List<Long> cids = terms.getBuckets().stream().
                    map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Map<String, Object>> categories = new ArrayList<>();
            categoryClient.queryCategoryByIds(cids).stream().forEach(category -> {
                Map<String, Object> map = new HashMap<>();
                map.put("cid", category.getId());
                map.put("name", category.getName());
                categories.add(map);
            });
            return categories;
        } catch (Exception e) {
            log.error("[分类服务异常]",e.getMessage());
            return null;
        }
    }

    /**
     * 创建索引
     * @param id
     */
    public void createIndex(Long id) {
        //查询spu
        Spu spu = goodsClient.querySpuById(id);
        //构建goods
        Goods goods = goodsBuilder(spu);
        //添加索引
        goodsRepository.save(goods);
    }

    /**
     * 删除所有
     * @param id
     */
    public void deleteIndex(Long id) {
        //删除索引
        goodsRepository.deleteById(id);
    }
}
