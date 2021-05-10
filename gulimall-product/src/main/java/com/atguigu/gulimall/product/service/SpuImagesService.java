package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.SpuImagesEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;

import java.util.Map;

/**
 * spu图片
 *
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-04-27 22:10:04
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

