package com.itheima.graduation.service;

import com.itheima.graduation.vo.ProblemVO;

import java.util.List;
import java.util.Map;

public interface ProblemRecordService {
    Integer submitAnswer(Long userId, Long problemId, String answer);
    
    List<ProblemVO> getWrongProblems(Long userId);
    
    Map<String, Object> getUserStats(Long userId);
    
    List<Map<String, Object>> getUserExerciseRecords(Long userId);
}
