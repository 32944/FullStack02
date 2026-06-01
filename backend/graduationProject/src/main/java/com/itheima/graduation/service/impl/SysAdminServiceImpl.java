package com.itheima.graduation.service.impl;

import com.itheima.graduation.constant.UserConstant;
import com.itheima.graduation.entity.SysAdmin;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.SysAdminMapper;
import com.itheima.graduation.service.SysAdminService;
import com.itheima.graduation.utils.JwtUtils;
import com.itheima.graduation.utils.Md5Utils;
import com.itheima.graduation.utils.ResultCode;
import com.itheima.graduation.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysAdminServiceImpl implements SysAdminService {

    private final JwtUtils jwtUtils;
    private final SysAdminMapper sysAdminMapper;

    @Override
    public LoginVO login(String username, String password) {
        SysAdmin admin = sysAdminMapper.selectByUsername(username);

        if (admin == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (admin.getStatus().equals(UserConstant.STATUS_DISABLED)) {
            throw new BusinessException("账号已被禁用");
        }

        String encryptedPassword = Md5Utils.encrypt(password);
        if (!encryptedPassword.equals(admin.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername());
        return new LoginVO(token, admin.getId(), admin.getUsername());
    }
}
