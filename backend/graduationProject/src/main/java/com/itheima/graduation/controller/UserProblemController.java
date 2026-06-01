package com.itheima.graduation.controller;

import com.itheima.graduation.entity.Notice;
import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.ProblemRecord;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.mapper.ProblemRecordMapper;
import com.itheima.graduation.service.NoticeService;
import com.itheima.graduation.service.ProblemRecordService;
import com.itheima.graduation.service.TrainingService;
import com.itheima.graduation.service.UserFavoriteService;
import com.itheima.graduation.service.UserWrongService;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.ProblemVO;
import com.itheima.graduation.vo.TrainingCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserProblemController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private UserWrongService userWrongService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ProblemRecordService problemRecordService;

    @Autowired
    private ProblemRecordMapper problemRecordMapper;

    @PostMapping("/favorite/add")
    public Result<Void> addFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long problemId = Long.valueOf(params.get("problemId").toString());
        userFavoriteService.add(userId, problemId);
        return Result.success();
    }

    @PostMapping("/favorite/remove")
    public Result<Void> removeFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long problemId = Long.valueOf(params.get("problemId").toString());
        userFavoriteService.remove(userId, problemId);
        return Result.success();
    }

    @GetMapping("/favorite/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long problemId) {
        boolean favorited = userFavoriteService.isFavorited(userId, problemId);
        return Result.success(favorited);
    }

    @GetMapping("/favorite/list")
    public Result<List<ProblemVO>> getFavorites(@RequestParam Long userId) {
        List<ProblemVO> list = userFavoriteService.getUserActiveFavorites(userId);
        return Result.success(list);
    }

    @GetMapping("/favorite/count")
    public Result<Integer> getFavoritesCount(@RequestParam Long userId) {
        int count = userFavoriteService.getActiveFavoritesCount(userId);
        return Result.success(count);
    }

    @PostMapping("/wrong/add")
    public Result<Void> addWrong(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long problemId = Long.valueOf(params.get("problemId").toString());
        userWrongService.add(userId, problemId);
        return Result.success();
    }

    @PostMapping("/wrong/remove")
    public Result<Void> removeWrong(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long problemId = Long.valueOf(params.get("problemId").toString());
        userWrongService.remove(userId, problemId);
        return Result.success();
    }

    @GetMapping("/wrong/check")
    public Result<Boolean> checkWrong(@RequestParam Long userId, @RequestParam Long problemId) {
        boolean wrong = userWrongService.isWrong(userId, problemId);
        return Result.success(wrong);
    }

    @GetMapping("/wrong/list")
    public Result<List<ProblemVO>> getWrongs(@RequestParam Long userId) {
        List<ProblemVO> list = userWrongService.getUserActiveWrongs(userId);
        return Result.success(list);
    }

    @GetMapping("/wrong/count")
    public Result<Integer> getWrongsCount(@RequestParam Long userId) {
        int count = userWrongService.getActiveWrongsCount(userId);
        return Result.success(count);
    }

    @GetMapping("/training/categories")
    public Result<List<TrainingCategoryVO>> getTrainingCategories(@RequestParam Long userId) {
        List<TrainingCategoryVO> list = trainingService.getUserTrainingCategories(userId);
        return Result.success(list);
    }

    @GetMapping("/training/questions")
    public Result<List<ProblemVO>> getTrainingQuestions(@RequestParam Long categoryId) {
        List<Problem> problems = problemMapper.selectByCategoryId(categoryId, 1);
        List<ProblemVO> voList = new java.util.ArrayList<>();
        for (Problem problem : problems) {
            ProblemVO vo = new ProblemVO();
            org.springframework.beans.BeanUtils.copyProperties(problem, vo);
            voList.add(vo);
        }
        return Result.success(voList);
    }

    @GetMapping("/notice/list")
    public Result<List<Notice>> getNoticeList() {
        List<Notice> list = noticeService.getActiveNotices();
        return Result.success(list);
    }

    @GetMapping("/problem/detail")
    public Result<ProblemVO> getProblemDetail(@RequestParam Long problemId) {
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            return Result.error("题目不存在");
        }
        ProblemVO vo = new ProblemVO();
        org.springframework.beans.BeanUtils.copyProperties(problem, vo);
        return Result.success(vo);
    }
    
    @GetMapping("/problem/answer")
    public Result<Map<String, Object>> getUserAnswer(@RequestParam Long userId, @RequestParam Long problemId) {
        ProblemRecord record = problemRecordMapper.selectByUserIdAndProblemId(userId, problemId);
        Map<String, Object> result = new HashMap<>();
        if (record != null) {
            result.put("answer", record.getAnsewr());
            result.put("status", record.getStatus());
        }
        return Result.success(result);
    }

    @PostMapping("/answer/submit")
    public Result<Map<String, Integer>> submitAnswer(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long problemId = Long.valueOf(params.get("problemId").toString());
        String answer = params.get("answer") != null ? params.get("answer").toString() : "";
        
        Integer status = problemRecordService.submitAnswer(userId, problemId, answer);
        return Result.success(Map.of("status", status));
    }

    @GetMapping("/wrong/problems")
    public Result<List<ProblemVO>> getWrongProblems(@RequestParam Long userId) {
        List<ProblemVO> list = problemRecordService.getWrongProblems(userId);
        return Result.success(list);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats(@RequestParam Long userId) {
        Map<String, Object> stats = problemRecordService.getUserStats(userId);
        return Result.success(stats);
    }

    @GetMapping("/exercise/records")
    public Result<List<Map<String, Object>>> getUserExerciseRecords(@RequestParam Long userId) {
        List<Map<String, Object>> records = problemRecordService.getUserExerciseRecords(userId);
        return Result.success(records);
    }
}
