package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.UserWrong;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.mapper.ProblemRecordMapper;
import com.itheima.graduation.mapper.UserWrongMapper;
import com.itheima.graduation.service.UserWrongService;
import com.itheima.graduation.vo.ProblemVO;
import com.itheima.graduation.vo.UserWrongVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserWrongServiceImpl implements UserWrongService {

    @Autowired
    private UserWrongMapper userWrongMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemRecordMapper problemRecordMapper;

    @Override
    public void add(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            throw new BusinessException("用户ID或题目ID不能为空");
        }

        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            throw new BusinessException("题目不存在");
        }

        UserWrong exist = userWrongMapper.selectByUserAndProblem(userId, problemId);
        if (exist != null) {
            exist.setWrongCount(exist.getWrongCount() + 1);
            userWrongMapper.update(exist);
        } else {
            UserWrong userWrong = new UserWrong();
            userWrong.setUserId(userId);
            userWrong.setProblemId(problemId);
            userWrong.setWrongCount(1);
            userWrongMapper.insert(userWrong);
        }
    }

    @Override
    public void remove(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            throw new BusinessException("用户ID或题目ID不能为空");
        }
        userWrongMapper.deleteByUserAndProblem(userId, problemId);
    }

    @Override
    public boolean isWrong(Long userId, Long problemId) {
        if (userId == null || problemId == null) {
            return false;
        }
        UserWrong exist = userWrongMapper.selectByUserAndProblem(userId, problemId);
        return exist != null;
    }

    @Override
    public List<ProblemVO> getUserWrongs(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Long> problemIds = problemRecordMapper.selectProblemIdsByUserAndStatus(userId, 0);
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
    public int getWrongsCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> problemIds = problemRecordMapper.selectProblemIdsByUserAndStatus(userId, 0);
        return problemIds.size();
    }

    @Override
    public List<ProblemVO> getUserActiveWrongs(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Long> problemIds = problemRecordMapper.selectProblemIdsByUserAndStatus(userId, 0);
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
    public int getActiveWrongsCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> problemIds = problemRecordMapper.selectProblemIdsByUserAndStatus(userId, 0);
        return problemIds.size();
    }

    @Override
    public List<UserWrongVO> getUserWrongDetails(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return problemRecordMapper.selectUserWrongDetails(userId);
    }
}
