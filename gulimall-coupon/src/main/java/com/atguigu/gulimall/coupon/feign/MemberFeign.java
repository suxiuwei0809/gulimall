package com.atguigu.gulimall.coupon.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gulimall-member")
public interface MemberFeign {
}
