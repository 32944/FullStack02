package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.UserClock;
import com.itheima.graduation.mapper.UserClockMapper;
import com.itheima.graduation.service.UserClockService;
import com.itheima.graduation.vo.UserClockVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserClockServiceImpl implements UserClockService {

    @Autowired
    private UserClockMapper userClockMapper;

    @Override
    public Map<String, Object> getClockInfo(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserClock todayClock = userClockMapper.selectTodayByUserId(userId, today);
        
        boolean isClocked = todayClock != null;
        int totalDays = userClockMapper.countByUserId(userId);
        int continuousDays = calculateContinuousDays(userId);
        
        result.put("isClocked", isClocked);
        result.put("totalDays", totalDays);
        result.put("continuousDays", continuousDays);
        
        return result;
    }

    @Override
    public boolean clockIn(Long userId) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserClock todayClock = userClockMapper.selectTodayByUserId(userId, today);
        
        if (todayClock != null) {
            return false;
        }
        
        UserClock userClock = new UserClock();
        userClock.setUserId(userId);
        userClock.setClockDate(today);
        userClock.setClockTime(new Date());
        userClock.setStatus(1);
        
        return userClockMapper.insert(userClock) > 0;
    }

    private int calculateContinuousDays(Long userId) {
        List<UserClock> records = userClockMapper.selectRecentByUserId(userId);
        if (records == null || records.isEmpty()) {
            return 0;
        }
        
        int continuousDays = 0;
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);
            Date checkDateObj = Date.from(checkDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            boolean found = false;
            for (UserClock record : records) {
                LocalDate recordDate = record.getClockDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (recordDate.equals(checkDate)) {
                    found = true;
                    break;
                }
            }
            
            if (found) {
                continuousDays++;
            } else if (i > 0) {
                break;
            }
        }
        
        return continuousDays;
    }

    @Override
    public List<Map<String, Object>> getClockHistory(Long userId) {
        List<UserClock> records = userClockMapper.selectRecentByUserId(userId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        
        if (records != null) {
            for (UserClock record : records) {
                Map<String, Object> item = new HashMap<>();
                LocalDate date = record.getClockDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                item.put("clockDate", String.format("%d-%02d-%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
                result.add(item);
            }
        }
        
        return result;
    }

    @Override
    public List<UserClockVO> getUserClockDetails(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<UserClock> userClocks = userClockMapper.selectRecentByUserId(userId);
        List<UserClockVO> voList = new ArrayList<>();

        if (userClocks != null) {
            for (UserClock userClock : userClocks) {
                UserClockVO vo = new UserClockVO();
                BeanUtils.copyProperties(userClock, vo);
                voList.add(vo);
            }
        }

        return voList;
    }
    
    @Override
    public List<Map<String, Object>> getDailyClockStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        List<Map<String, Object>> rawStats = userClockMapper.selectDailyClockStats(start, end);
        
        Map<String, Integer> statsMap = new HashMap<>();
        for (Map<String, Object> stat : rawStats) {
            String date = stat.get("date").toString();
            int count = ((Number) stat.get("count")).intValue();
            statsMap.put(date, count);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.toString();
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", statsMap.getOrDefault(dateStr, 0));
            result.add(item);
        }
        
        return result;
    }
}