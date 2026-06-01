package com.itheima.graduation.service;

import com.itheima.graduation.entity.SysAdmin;
import com.itheima.graduation.vo.LoginVO;

public interface SysAdminService {
    LoginVO login(String username, String password);
}