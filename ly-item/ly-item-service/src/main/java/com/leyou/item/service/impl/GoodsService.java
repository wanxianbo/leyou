package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsService implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public PageResult<Spu> querySpuVoByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "% ");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        List<Spu> spus = goodsMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //转化为cname，bname
        loadCategoryAndBrandName(spus);
        return new PageResult<>(pageInfo.getTotal(),spus);
    }

    @Transactional
    @Override
    public void saveGoods(Spu spu) {
        //1.向spu表插入数据
        spu.setId(null);
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        //开始插入
        int count = goodsMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
        }
        //2.向spuDetail表插入数据
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        //开始插入
        count = spuDetailMapper.insert(spuDetail);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
        }
        //3.向sku表插入数据
        //List<Sku> skus = spu.getSkus();
        /*//创建一个集合，用来装stock的对象
        List<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus) {
            sku.setId(null);
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            //开始插入
            count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
            }
            //新建stock对象
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            //向stocks集合添加stock对象
            stocks.add(stock);
        }
        //批量添加stock数据
        count = stockMapper.insertList(stocks);
        if (count != stocks.size()) {
            throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
        }*/
        //保存sku和stock信息
        saveSkuAndStock(spu);
        //发送消息到mq
        sendMessage(spu.getId(), "insert");
    }

    private void sendMessage(Long id, String type) {
        //发送消息
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    @Override
    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 根据spu_id查询sku
     *
     * @param id
     * @return
     */
    @Override
    public List<Sku> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> list = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }

        List<Long> skuIds = list.stream().map(s -> s.getId()).collect(Collectors.toList());
        //查询库存量-stock表
        List<Stock> stockList = stockMapper.selectByIdList(skuIds);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(s -> s.getSkuId(), s -> s.getStock()));
        list.forEach(s -> s.setStock(stockMap.get(s.getId())));
        return list;
    }

    @Transactional
    @Override
    public void updateGoods(Spu spu) {
        //1.查询sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //2.删除sku，stock
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        int count = skuMapper.delete(sku);
        if (count != skuList.size()) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
        List<Long> ids = skuList.stream().map(s -> s.getId()).collect(Collectors.toList());
        stockMapper.deleteByIdList(ids);
        //3.更新spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        count = goodsMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_ERROR);
        }
        //4.更新detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        //5.新增sku和stock
        saveSkuAndStock(spu);

        //发送消息到mq
        sendMessage(spu.getId(),"update");
    }

    /**
     * 根据spuId删除商品
     *
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteGoods(Long spuId) {
        //根据spuId查询sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        //删除sku
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        int count = skuMapper.delete(sku);
        if (count != skuList.size()) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
        //删除stock
        List<Long> ids = skuList.stream().map(s -> s.getId()).collect(Collectors.toList());
        count = stockMapper.deleteByIdList(ids);
        if (count != ids.size()) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
        //删除spu和detail
        count = goodsMapper.deleteByPrimaryKey(spuId);
        if (count != 1) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }
        count = spuDetailMapper.deleteByPrimaryKey(spuId);
        if (count != 1) {
            throw new LyException(ExceptionEnum.DELETE_ERROR);
        }

        //发送消息到mq
        sendMessage(spuId,"delete");
    }

    /**
     * 根据spuId更新saleable字段
     * @param spuId
     */
    @Transactional
    @Override
    public void updateSaleable(Long spuId) {
        Spu spu = goodsMapper.selectByPrimaryKey(spuId);
        if (spu.getSaleable()) {
            spu.setSaleable(false);
        } else {
            spu.setSaleable(true);
        }
        spu.setId(spuId);
        int count = goodsMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_ERROR);
        }
    }

    @Override
    public Spu querySpuById(Long spuId) {
        Spu spu = goodsMapper.selectByPrimaryKey(spuId);
        if (spu == null) {
            throw new LyException(ExceptionEnum.GOOD_NOT_FOUND);
        }
        return spu;
    }

    private void saveSkuAndStock(Spu spu) {
        List<Stock> stockList = new ArrayList<>();
        spu.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count = skuMapper.insertSelective(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
            }
            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        });
        //批量添加stock数据
        int count = stockMapper.insertList(stockList);
        if (count != stockList.size()) {
            throw new LyException(ExceptionEnum.GOOD_SAVE_ERROR);
        }

    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //"[1,2,3,4]"
            //String s = spuVo.getCid1() +","+ spuVo.getCid2() + ","+ spuVo.getCid3();
            BeanUtils.copyProperties(spu,spu);
            //1.得到cname
            String cnames = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(c -> c.getName())
                    .collect(Collectors.joining("/"));
            spu.setCname(cnames);
            //2.得到bname
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spu.setBname(brand.getName());
        }
    }




}
