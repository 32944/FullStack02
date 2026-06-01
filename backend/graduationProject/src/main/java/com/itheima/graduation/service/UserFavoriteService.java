package com.itheima.graduation.service;

import com.itheima.graduation.entity.UserFavorite;
import com.itheima.graduation.vo.ProblemVO;

import java.util.List;
import java.util.Map;

public interface UserFavoriteService {

    void add(Long userId, Long problemId);

    void remove(Long userId, Long problemId);

    boolean isFavorited(Long userId, Long problemId);

    List<ProblemVO> getUserFavorites(Long userId);

    List<ProblemVO> getUserActiveFavorites(Long userId);

    int getFavoritesCount(Long userId);

    int getActiveFavoritesCount(Long userId);
    
    List<Map<String, Object>> getTopFavorites(int limit);
}
