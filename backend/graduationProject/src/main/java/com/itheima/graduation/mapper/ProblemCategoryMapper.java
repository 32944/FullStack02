package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.ProblemCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProblemCategoryMapper {

    List<ProblemCategory> selectAll();

    ProblemCategory selectById(Long id);

    ProblemCategory selectByName(@Param("name") String name);
}
