package com.atguigu.gulimall.member.controller;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.exception.BizCodeEnum;
import com.atguigu.common.vo.GiteeUser;
import com.atguigu.gulimall.member.Exception.PhoneExistException;
import com.atguigu.gulimall.member.Exception.UserNameExistException;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.feign.CouponFeign;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-05-10 23:16:23
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeign  couponFeign;
    @RequestMapping("/testcoupon")
    //@RequiresPermissions("member:member:list")
    public R testcoupon(){
        return couponFeign.getCouponLists();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 注册
     */
     @PostMapping("/register")
     public R register(@RequestBody MemberRegisterVo vo){
        try {
            memberService.register(vo);
        }catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UserNameExistException e){
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(),BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }
    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity  memberEntity=  memberService.login(vo);
        if(memberEntity!=null){
            return R.ok().put("data",memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGINACTT_PASSWORD_ERROR.getCode()
            ,BizCodeEnum.LOGINACTT_PASSWORD_ERROR.getMsg());
        }
    }


    @PostMapping("/oath/login")
    public R oathLogin(@RequestBody GiteeUser  giteeUser){
        MemberEntity  memberEntity=  memberService.oathLogin(giteeUser);
        if(memberEntity!=null){
            return R.ok().put("data",memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGINACTT_PASSWORD_ERROR.getCode()
                    ,BizCodeEnum.LOGINACTT_PASSWORD_ERROR.getMsg());
        }
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
