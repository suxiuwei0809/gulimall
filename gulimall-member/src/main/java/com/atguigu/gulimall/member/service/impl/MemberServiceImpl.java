package com.atguigu.gulimall.member.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.vo.GiteeUser;
import com.atguigu.gulimall.member.Exception.EmailExistException;
import com.atguigu.gulimall.member.Exception.PhoneExistException;
import com.atguigu.gulimall.member.Exception.UserNameExistException;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao  memberLevelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        BeanUtils.copyProperties(vo,memberEntity);
        MemberLevelEntity memberLevelEntity=memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        this.checkPhoneUnique(vo.getPhone());
        this.checkPhoneUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());    // 密码加密处理
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(vo.getPassword());
        //密码校验
        //boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), encodePassword);
        memberEntity.setPassword(encodePassword);
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkEmailUnique(String email) throws EmailExistException {

        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("email", email));
        if(count>0){
            throw new   EmailExistException();
        }
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile",phone));
        if(count>0){
            throw new   PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        Integer count =this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username",userName));
        if(count>0){
            throw new   UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {

        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginAccount()).or().eq("mobile", vo.getLoginAccount()));
        if(memberEntity!=null){
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), memberEntity.getPassword());
            if(matches){
                return  memberEntity;
            }else {
                return null;
            }
        }else {
            return null;
        }

    }

    @Override
    public MemberEntity oathLogin(GiteeUser giteeUser) {
       MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", giteeUser.getId()));
       if(memberEntity!=null){
           memberEntity.setAccess_token(giteeUser.getAccess_token());
           memberEntity.setExpires_in(giteeUser.getExpires_in()+"");
           this.baseMapper.updateById(memberEntity);
           return  memberEntity;
       }else{
           MemberEntity memberEntity1 = new MemberEntity();
           MemberLevelEntity memberLevelEntity=memberLevelDao.getDefaultLevel();
           memberEntity1.setLevelId(memberLevelEntity.getId());
           memberEntity1.setAccess_token(giteeUser.getAccess_token());
           memberEntity1.setExpires_in(giteeUser.getExpires_in()+"");
           memberEntity1.setNickname(giteeUser.getName());
           memberEntity1.setSocial_uid(giteeUser.getId()+"");
           memberEntity1.setEmail(giteeUser.getEmail());
           memberEntity1.setUsername(giteeUser.getName());
           this.baseMapper.insert(memberEntity1);
           return  memberEntity1;
       }
    }
}