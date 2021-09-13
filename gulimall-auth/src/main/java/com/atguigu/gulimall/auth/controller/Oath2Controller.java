package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.GiteeUser;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.common.vo.TokenInfo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@Slf4j
public class Oath2Controller {

    @Autowired
    MemberFeignService memberService;


    @GetMapping("gitee/success")
    public   String   giteeLogin(@RequestParam(value = "code" ,required = true) String code, HttpSession session){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("code",code);
        hashMap.put("grant_type","authorization_code");
        hashMap.put("client_id","7f9886f435f314f65dfd811516fac11643b765b4a386e453fb5fe4d95e8e86c9");
        hashMap.put("redirect_uri","http://auth.gulimall.com/gitee/success");
        hashMap.put("client_secret","c8be4f6011f28a53a7a1589043bcbd1358a02fb2eae8701cc24cd6fdda3a0b6d");
         try {
          HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), null, hashMap);
             log.info("状态码="+response.getStatusLine().getStatusCode());
            if(response.getStatusLine().getStatusCode()==200){
                String json = EntityUtils.toString(response.getEntity());
                TokenInfo tokenInfo = JSON.parseObject(json, TokenInfo.class);
               // https://gitee.com/api/v5/user
                HashMap<String, String> query = new HashMap<>();
                query.put("access_token",tokenInfo.getAccess_token());
                HttpResponse response1 = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get",  new HashMap<>(), query);
                String json1 = EntityUtils.toString(response1.getEntity());
                GiteeUser giteeUser = JSON.parseObject(json1, GiteeUser.class);
                log.info("登录成功！"+json1);
                BeanUtils.copyProperties(giteeUser,tokenInfo);
                R r = memberService.oathLogin( giteeUser);
                if(r.getCode()==0){
                    MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                    });
                    //session id  放redis 唯一一份  可以解决集群   没有解决跨域
                    // 通过配置文件指定序列化机制  放大session作用域
                    session.setAttribute(AuthServerConstant.LOGIN_USER,data);
                    log.info(data.getNickname()+"登录成功！");
                    return "redirect:http://gulimall.com";
                }

            }else{
                log.info("登录失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:http://gulimall.com";
    }


}
