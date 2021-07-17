package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTO;
import com.atguigu.common.to.SpuBoundTO;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 开启远程调用
 * 1、注册到注册中心 EnableDiscoveryClient
 * 2、开启远程调用功能EnableFeignClients
 * 3、指定调用哪个服务FeignClient
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
        //@RequestBody 以json格式发送请求
       @PostMapping("coupon/spubounds/save")
       R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);
       @PostMapping("coupon/skufullreduction/saveInfo")
       R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
