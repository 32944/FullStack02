package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.UserFavorite;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.mapper.UserFavoriteMapper;
import com.itheima.graduation.service.UserFavoriteService;
import com.itheima.graduation.vo.ProblemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public void add(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            throw new BusinessException("用户ID或题目ID不能为空");
        }

        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            throw new BusinessException("题目不存在");
        }

        UserFavorite exist = userFavoriteMapper.selectByUserAndProblem(userId, problemId);
        if (exist != null) {
            throw new BusinessException("该题目已收藏");
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setProblemId(problemId);
        userFavoriteMapper.insert(userFavorite);
    }

    @Override
    public void remove(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            throw new BusinessException("用户ID或题目ID不能为空");
        }
        userFavoriteMapper.deleteByUserAndProblem(userId, problemId);
    }

    @Override
    public boolean isFavorited(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            return false;
        }
        UserFavorite exist = userFavoriteMapper.selectByUserAndProblem(userId, problemId);
        return exist != null;
    }

    @Override
    public List<ProblemVO> getUserFavorites(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Long> problemIds = userFavoriteMapper.selectProblemIdsByUser(userId);
        List<ProblemVO> voList = new ArrayList<>();

        for (Long problemId : problemIds) {
            Problem problem = problemMapper.selectById(problemId);
            if (problem != null && problem.getStatus() == 1) {
                ProblemVO vo = new ProblemVO();
                BeanUtils.copyProperties(problem, vo);
                voList.add(vo);
            }
        }

        return voList;
    }

    @Override
    public int getFavoritesCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userFavoriteMapper.countByUser(userId);
    }

    @Override
    public List<ProblemVO> getUserActiveFavorites(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Long> problemIds = userFavoriteMapper.selectActiveProblemIdsByUser(userId);
        List<ProblemVO> voList = new ArrayList<>();

        for (Long problemId : problemIds) {
            Problem problem = problemMapper.selectById(problemId);
            if (problem != null) {
                ProblemVO vo = new ProblemVO();
                BeanUtils.copyProperties(problem, vo);
                voList.add(vo);
            }
        }

        return voList;
    }

    @Override
    public int getActiveFavoritesCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userFavoriteMapper.countActiveByUser(userId);
    }

    @Override
    public List<Map<String, Object>> getTopFavorites(int limit) {
        return userFavoriteMapper.selectTopFavorites(limit);
    }
}
