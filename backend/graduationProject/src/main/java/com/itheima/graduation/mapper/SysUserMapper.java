package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    int insert(SysUser sysUser);

    int updateById(SysUser sysUser);

    int deleteById(Long id);

    SysUser selectById(Long id);

    SysUser selectByOpenid(String openid);

    List<SysUser> selectPage(@Param("nickname") String nickname,
                               @Param("targetJob") String targetJob,
                               @Param("status") Integer status,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);

    int count(@Param("nickname") String nickname,
              @Param("targetJob") String targetJob,
              @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updateLevel(@Param("id") Long id, @Param("currentLevel") Integer currentLevel);
}
