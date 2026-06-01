package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    List<Notice> selectByStatus(Integer status);
}
