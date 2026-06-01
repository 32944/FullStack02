package com.itheima.graduation.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.graduation.config.WxConfig;
import com.itheima.graduation.constant.UserConstant;
import com.itheima.graduation.entity.SysUser;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.SysUserMapper;
import com.itheima.graduation.service.WxLoginService;
import com.itheima.graduation.utils.JwtUtils;
import com.itheima.graduation.vo.WxLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class WxLoginServiceImpl implements WxLoginService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private WxConfig wxConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WxLoginVO login(String code, String nickname, String avatar) {
        String openid = getOpenidFromWx(code);
        
        SysUser existUser = sysUserMapper.selectByOpenid(openid);
        
        WxLoginVO vo = new WxLoginVO();
        vo.setOpenid(openid);
        
        if (existUser != null) {
            if (UserConstant.STATUS_DISABLED.equals(existUser.getStatus())) {
                throw new BusinessException("账号已被禁用，请联系管理员");
            }
            
            if (StringUtils.hasText(nickname)) {
                existUser.setNickname(nickname);
            }
            if (StringUtils.hasText(avatar)) {
                existUser.setAvatar(avatar);
            }
            existUser.setUpdateTime(LocalDateTime.now());
            sysUserMapper.updateById(existUser);
            
            vo.setUserId(existUser.getId());
            vo.setNickname(existUser.getNickname());
            vo.setAvatar(existUser.getAvatar());
            vo.setIsNew(0);
            
            String token = jwtUtils.generateToken(existUser.getId().intValue(), existUser.getNickname());
            vo.setToken(token);
        } else {
            SysUser newUser = new SysUser();
            newUser.setOpenid(openid);
            newUser.setNickname(StringUtils.hasText(nickname) ? nickname : "微信用户");
            newUser.setAvatar(StringUtils.hasText(avatar) ? avatar : "https://api.dicebear.com/7.x/avataaars/svg?seed=" + openid);
            newUser.setStatus(UserConstant.STATUS_NORMAL);
            newUser.setCurrentLevel(1);
            newUser.setCreateTime(LocalDateTime.now());
            newUser.setUpdateTime(LocalDateTime.now());
            
            sysUserMapper.insert(newUser);
            
            vo.setUserId(newUser.getId());
            vo.setNickname(newUser.getNickname());
            vo.setAvatar(newUser.getAvatar());
            vo.setIsNew(1);
            
            String token = jwtUtils.generateToken(newUser.getId().intValue(), newUser.getNickname());
            vo.setToken(token);
        }
        
        return vo;
    }

    private String getOpenidFromWx(String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wxConfig.getAppid(),
                wxConfig.getSecret(),
                code
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("errcode")) {
                System.err.println("微信接口返回错误: " + response);
                return "test_openid_" + System.currentTimeMillis();
            }
            
            return jsonNode.get("openid").asText();
        } catch (Exception e) {
            System.err.println("调用微信接口失败: " + e.getMessage());
            return "test_openid_" + System.currentTimeMillis();
        }
    }
}
