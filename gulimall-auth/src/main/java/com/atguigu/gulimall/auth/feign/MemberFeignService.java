package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.common.vo.GiteeUser;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("member/member/register")
    public R register(@RequestBody UserRegisterVo vo);

    @PostMapping("member/member/login")
    public R login(UserLoginVo userLoginVo);

    @PostMapping("member/member/oath/login")
    public R  oathLogin(@RequestBody GiteeUser giteeUser);


    }
