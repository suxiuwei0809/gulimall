package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.exception.BizCodeEnum;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    ThirdPartyFeignService thirdPartyService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MemberFeignService memberService;

    @RequestMapping("login.html")
    public String login() {
        System.out.println("---------login------------");
        return "login";
    }

    @RequestMapping("reg.html")
    public String reg() {
        return "reg";
    }

    @RequestMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        //1、接口防刷
        //2、验证验证码
        String code_time = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        //同一个手机号60s内发一次
        if (!StringUtils.isEmpty(code_time)) {
            long l = Long.parseLong(code_time.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();

        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        R r = thirdPartyService.sendCode(phone, code);
        return r;
    }

    //@Valid UserRegisterVo userRegisterVo  校验内容
    //BindResult result  校验结果
    // Model  model,
    // RedirectAttributes redirectAttributes  重定向访问域
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //          HashMap<Object, Object> errors = new HashMap<>();
//           result.getFieldErrors().stream().map(err->{
//               String field = err.getField();
//               String defaultMessage = err.getDefaultMessage();
//               errors.put(field,defaultMessage);
//               return null;
//           });
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http//auth.gulimall.com/reg.html";
        }
        String code = userRegisterVo.getCode();
        String sms_code = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
        if (StringUtils.isEmpty(sms_code)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", "验证码为空");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http//auth.gulimall.com/reg.html";
        } else {
            if (code.equals(sms_code.split("_")[0])) {
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
                //验证通过后注册
                R r = memberService.register(userRegisterVo);
                if (r.getCode() == 0) {
                    //成功
                    return "login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("key",new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", "验证码为空");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo  userLoginVo, HttpSession  session){
        R  r= memberService.login(userLoginVo);
        if(r.getCode()==0){
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
        }
        return "redirect:http://gulimall.com";
    }
}
