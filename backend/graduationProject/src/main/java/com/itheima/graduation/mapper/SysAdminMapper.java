package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.SysAdmin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAdminMapper {
    SysAdmin selectByUsername(String username);
}
