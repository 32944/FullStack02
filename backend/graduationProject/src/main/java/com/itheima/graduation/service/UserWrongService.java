package com.itheima.graduation.service;

import com.itheima.graduation.entity.UserWrong;
import com.itheima.graduation.vo.ProblemVO;
import com.itheima.graduation.vo.UserWrongVO;

import java.util.List;

public interface UserWrongService {

    void add(Long userId, Long problemId);

    void remove(Long userId, Long problemId);

    boolean isWrong(Long userId, Long problemId);

    List<ProblemVO> getUserWrongs(Long userId);

    List<ProblemVO> getUserActiveWrongs(Long userId);

    int getWrongsCount(Long userId);

    int getActiveWrongsCount(Long userId);

    List<UserWrongVO> getUserWrongDetails(Long userId);
}
