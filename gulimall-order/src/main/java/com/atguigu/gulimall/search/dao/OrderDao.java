package com.atguigu.gulimall.search.dao;

import com.atguigu.gulimall.search.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-05-10 23:21:25
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
