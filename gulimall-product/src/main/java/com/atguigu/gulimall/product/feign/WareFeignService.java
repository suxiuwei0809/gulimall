package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 开启远程调用
 * 1、注册到注册中心 EnableDiscoveryClient
 * 2、开启远程调用功能EnableFeignClients
 * 3、指定调用哪个服务FeignClient
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping("ware/waresku/hasstock")
    R< List<SkuHasStockVo> > getSkusHasStock(@RequestBody List<Long> skuIds);
}
