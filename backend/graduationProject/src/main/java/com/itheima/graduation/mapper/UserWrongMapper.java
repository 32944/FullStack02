package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.UserWrong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserWrongMapper {

    int insert(UserWrong userWrong);

    int update(UserWrong userWrong);

    int deleteByUserAndProblem(@Param("userId") Long userId, @Param("problemId") Long problemId);

    UserWrong selectByUserAndProblem(@Param("userId") Long userId, @Param("problemId") Long problemId);

    List<Long> selectProblemIdsByUser(@Param("userId") Long userId);

    List<Long> selectActiveProblemIdsByUser(@Param("userId") Long userId);

    List<UserWrong> selectByUser(@Param("userId") Long userId);

    int countByUser(@Param("userId") Long userId);

    int countActiveByUser(@Param("userId") Long userId);
}
