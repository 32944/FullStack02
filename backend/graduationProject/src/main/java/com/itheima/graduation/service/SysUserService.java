package com.itheima.graduation.service;

import com.itheima.graduation.dto.UserDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.vo.UserDetailVO;
import com.itheima.graduation.vo.UserVO;

public interface SysUserService {

    void add(UserDTO userDTO);

    void update(UserDTO userDTO);

    void delete(Long id);

    UserVO getById(Long id);

    PageResult<UserVO> page(UserDTO userDTO);

    void updateStatus(Long id, Integer status);

    void updateLevel(Long id, Integer currentLevel);

    void updateTargetJob(Long id, String targetJob);

    UserDetailVO getUserDetail(Long id);
}
