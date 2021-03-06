package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.es.SkuHasStockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * εεεΊε­
 *
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-05-10 23:27:19
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

}

