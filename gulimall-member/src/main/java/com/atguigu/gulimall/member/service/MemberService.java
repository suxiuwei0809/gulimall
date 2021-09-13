package com.atguigu.gulimall.member.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.GiteeUser;
import com.atguigu.gulimall.member.Exception.EmailExistException;
import com.atguigu.gulimall.member.Exception.PhoneExistException;
import com.atguigu.gulimall.member.Exception.UserNameExistException;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author suxiuwei
 * @email suxiuwei0809@outlook.com
 * @date 2021-05-10 23:16:23
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    void checkEmailUnique( String  email)  throws EmailExistException;
    void checkPhoneUnique( String  phone) throws PhoneExistException;
    void checkUserNameUnique( String  userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity oathLogin(GiteeUser giteeUser);
}

