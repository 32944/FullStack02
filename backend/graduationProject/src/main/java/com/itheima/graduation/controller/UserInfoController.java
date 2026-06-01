package com.itheima.graduation.controller;

import com.itheima.graduation.service.SysUserService;
import com.itheima.graduation.service.UserClockService;
import com.itheima.graduation.service.UserFavoriteService;
import com.itheima.graduation.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private UserClockService userClockService;
    
    @Autowired
    private UserFavoriteService userFavoriteService;

    @PostMapping("/updateTargetJob")
    public Result<Void> updateTargetJob(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String targetJob = (String) params.get("targetJob");
        
        sysUserService.updateTargetJob(id, targetJob);
        return Result.success();
    }
    
    @GetMapping("/clock/info")
    public Result<Map<String, Object>> getClockInfo(@RequestParam Long userId) {
        Map<String, Object> info = userClockService.getClockInfo(userId);
        return Result.success(info);
    }
    
    @PostMapping("/clock/in")
    public Result<Void> clockIn(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        boolean success = userClockService.clockIn(userId);
        if (success) {
            return Result.success();
        } else {
            return Result.error("今日已签到");
        }
    }
    
    @GetMapping("/clock/history")
    public Result<List<Map<String, Object>>> getClockHistory(@RequestParam Long userId) {
        List<Map<String, Object>> history = userClockService.getClockHistory(userId);
        return Result.success(history);
    }
    
    @GetMapping("/clock/daily-stats")
    public Result<List<Map<String, Object>>> getDailyClockStats(@RequestParam(defaultValue = "7") int days) {
        List<Map<String, Object>> stats = userClockService.getDailyClockStats(days);
        return Result.success(stats);
    }
    
    @GetMapping("/favorite/top")
    public Result<List<Map<String, Object>>> getTopFavorites(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> topList = userFavoriteService.getTopFavorites(limit);
        return Result.success(topList);
    }
}
