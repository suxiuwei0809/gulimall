package com.atguigu.gulimall.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.search.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-05-10 23:21:25
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
