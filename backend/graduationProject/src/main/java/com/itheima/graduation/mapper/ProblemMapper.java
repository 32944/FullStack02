package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProblemMapper {

    int insert(Problem problem);

    int insertBatch(List<Problem> problems);

    int updateById(Problem problem);

    int deleteById(Long id);

    Problem selectById(Long id);

    List<Problem> selectPage(@Param("title") String title,
                              @Param("categoryId") Long categoryId,
                              @Param("status") Integer status,
                              @Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize);

    List<Problem> selectByCategoryId(@Param("categoryId") Long categoryId, @Param("status") Integer status);

    int count(@Param("title") String title,
              @Param("categoryId") Long categoryId,
              @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
