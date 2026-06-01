package com.itheima.graduation.service;

import com.itheima.graduation.vo.WxLoginVO;

public interface WxLoginService {
    
    WxLoginVO login(String code, String nickname, String avatar);
}
