package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
     @Autowired
     WareSkuDao wareSkuDao;

     @Autowired
     ProductFeignService productFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String sku_id = (String)params.get("sku_id");
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(sku_id))
            wrapper.eq("sku_id",sku_id);

        String ware_id = (String)params.get("ware_id");
        if(!StringUtils.isEmpty(ware_id))
        wrapper.eq("ware_id",ware_id);
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("ware_id", wareId).eq("sku_id", skuId));
       //如果之前没有添加过库存为null
        if(wareSkuEntities==null||wareSkuEntities.size()==0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询商品的名字
            //这里就设置一个商品的名字不需要回滚
            try{
            R info = productFeignService.Info(skuId);
                Map<String,Object> data = (Map<String,Object>)info.get("skuInfo");
                if(info.getCode()==0){
                wareSkuEntity.setSkuName((String)data.get("skuName"));
                    }
            }catch (Exception e){}
            //wareSkuEntity.setSkuName("");
            this.save(wareSkuEntity);
        }
        //如果之前添加过库存 库存加起来
        wareSkuDao.addStock(skuId, wareId,skuNum);
    }

}