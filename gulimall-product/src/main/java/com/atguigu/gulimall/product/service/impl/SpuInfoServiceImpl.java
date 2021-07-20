package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTO;
import com.atguigu.common.to.SpuBoundTO;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.es.SkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {

        this.baseMapper.insert(spuInfoEntity);
    }

    @Transactional
    @Override
    public void savSpuInfo(SpuSaveVo spuSaveVo) {
        /*** 1、保存spu基本信息 pms_spu_info*/
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setCreateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        /***2、保存spu的描述图片 pms_spu_info_desc*/
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);
        /***3、保存spu的图片集 pms_spu_images*/
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        /***4、保存spu的规格参数 pms_product_attr_value*/
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            AttrEntity attr = attrService.getById(item.getAttrId());
            productAttrValueEntity.setAttrName(attr.getAttrName());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
        /**
         *5、 保存spu的积分信息gulimall_sms ->sms_spu_bouds*/
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTO);
        if (r1.getCode() != 0) {
            log.error("远程保存Sku优惠信息失败");
        }
        /**
         *6.1)、 保存当前spu对应的所有sku的信息：pms_sku_info*/
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(skuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                /**
                 *6.2)、 保存sku的图片信息  ：pms_sku_images*/
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> collect1 = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(img -> {
                    //返回的true 留下   返回false剔除
                    return StringUtils.isNotEmpty(img.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(collect1);


                /**
                 *6.3)、 保存sku的销售属性  ：pms_sku_sale_attr_value*/
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrList = attr.stream().map((saleAttr) -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(saleAttr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrList);


                /**
                 *6.3)、 保存sku的优惠信息和满减信息：gulimall_sms->sms_sku_ladder sms_sku_full_reduction*/
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item, skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                //有满减或打折扣不为0 的才保存
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTO);
                    if (r.getCode() != 0) {
                        log.error("远程保存Sku优惠信息失败");
                    }
                }


            });
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.and((w) -> {
                w.eq("publish_status", status);
            });
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)&&!"0".equals(brandId)) {
            wrapper.and((w) -> {
                w.eq("brand_id", brandId);
            });
        }
        String catalogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catalogId)&&!"0".equals(catalogId)) {
            wrapper.and((w) -> {
                w.eq("catalog_id", catalogId);
            });
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        ArrayList<SkuEsModel> products = new ArrayList<>();
        /**
         *   查询当前sku所有可以被检索的规格属性  sku->spu->关联属性->属性是否需要查询
         */
        //查出所有spu下的属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseListForSpu(spuId);
        List<Long> baseAttrIds = baseAttrs.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //再在属性表里查出所有可以搜索的属性
        List<Long>   searchAttrIds =attrService.selectSearchAttrIds(baseAttrIds);

        HashSet<Object> idsSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList= baseAttrs.stream().filter(item -> {
            return idsSet.contains(item.getAttrId());//得到规格参数
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());


        /**
         * 查出所有的sku信息
         */
            List<SkuInfoEntity>   skus   =skuInfoService.getSkuBySpuId(spuId);
        /**
         * 发送远程调用查询是否有库存
         * 批量查询
         */
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        R<List<SkuHasStockVo>> skusHasStock=null;
      try {
          skusHasStock = wareFeignService.getSkusHasStock(skuIds);
      }catch (Exception e){
          log.error("远程查询异常");
      }

        List<SkuHasStockVo> hasStockVoList =   skusHasStock.getData();
        Map<Long, Boolean> hasStockMap = hasStockVoList.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        R<List<SkuHasStockVo>> finalSkusHasStock = skusHasStock;
        List<SkuEsModel> collect = skus.stream().map(item -> {
            //1、组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsModel);
            skuEsModel.setSkuPrice(item.getPrice());
            skuEsModel.setSkuImg(item.getSkuDefaultImg());
            /**
             * 发送远程调用查询是否有库存
             */
            if (finalSkusHasStock == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(hasStockMap.get(item.getSkuId()));
            }
            /**
             * 评分系统  暂时不处理
             */
            //todo 暂时给0热度
            skuEsModel.setHotScore(0L);
            /**
             * 品牌名字 和图片
             */
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            //这里是规格参数
            skuEsModel.setAttrs(attrsList);

            return skuEsModel;

        }).collect(Collectors.toList());
        R r = searchFeignService.ProductStatusUp(collect);
        if (r.getCode()==0){
            /**
             * 上架成功修改为上架状态
             */

            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            //todo  重复调用会出现接口幂等性
        }
    }

}