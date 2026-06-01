package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserFavoriteMapper {

    int insert(UserFavorite userFavorite);

    int deleteByUserAndProblem(@Param("userId") Long userId, @Param("problemId") Long problemId);

    UserFavorite selectByUserAndProblem(@Param("userId") Long userId, @Param("problemId") Long problemId);

    List<Long> selectProblemIdsByUser(@Param("userId") Long userId);

    List<Long> selectActiveProblemIdsByUser(@Param("userId") Long userId);

    List<UserFavorite> selectByUser(@Param("userId") Long userId);

    int countByUser(@Param("userId") Long userId);

    int countActiveByUser(@Param("userId") Long userId);
    
    List<Map<String, Object>> selectTopFavorites(@Param("limit") Integer limit);
}
