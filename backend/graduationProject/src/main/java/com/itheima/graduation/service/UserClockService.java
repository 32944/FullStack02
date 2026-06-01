package com.itheima.graduation.service;

import com.itheima.graduation.vo.UserClockVO;

import java.util.List;
import java.util.Map;

public interface UserClockService {
    Map<String, Object> getClockInfo(Long userId);
    
    boolean clockIn(Long userId);
    
    List<Map<String, Object>> getClockHistory(Long userId);

    List<UserClockVO> getUserClockDetails(Long userId);
    
    List<Map<String, Object>> getDailyClockStats(int days);
}