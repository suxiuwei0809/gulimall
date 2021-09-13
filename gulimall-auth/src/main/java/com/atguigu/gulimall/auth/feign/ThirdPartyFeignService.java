package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 开启远程调用
 * 1、注册到注册中心 EnableDiscoveryClient
 * 2、开启远程调用功能EnableFeignClients
 * 3、指定调用哪个服务FeignClient
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {
    @RequestMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone")String phone, @RequestParam("code") String code);
}