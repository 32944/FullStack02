package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.UserClock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserClockMapper {
    int insert(UserClock userClock);
    
    int countByUserId(Long userId);
    
    int countContinuousDays(@Param("userId") Long userId, @Param("startDate") Date startDate);
    
    UserClock selectTodayByUserId(@Param("userId") Long userId, @Param("today") Date today);
    
    List<UserClock> selectRecentByUserId(Long userId);
    
    List<Map<String, Object>> selectDailyClockStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}