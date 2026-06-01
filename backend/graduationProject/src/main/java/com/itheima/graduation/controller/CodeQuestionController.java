package com.itheima.graduation.controller;

import com.itheima.graduation.dto.CodeJudgeDTO;
import com.itheima.graduation.entity.CodeQuestion;
import com.itheima.graduation.entity.UserRecord;
import com.itheima.graduation.service.CodeQuestionService;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.CodeJudgeResultVO;
import com.itheima.graduation.vo.CodeQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
public class CodeQuestionController {

    @Autowired
    private CodeQuestionService codeQuestionService;

    @GetMapping("/question/{id}")
    public Result<CodeQuestionVO> getQuestion(@PathVariable Long id) {
        return Result.success(codeQuestionService.getById(id));
    }

    @GetMapping("/questions")
    public Result<List<CodeQuestion>> getAllQuestions() {
        return Result.success(codeQuestionService.getAllQuestions());
    }

    @PostMapping("/judge")
    public Result<CodeJudgeResultVO> judgeCode(@RequestHeader(value = "userId", required = false) String userIdStr,
                                               @RequestBody CodeJudgeDTO codeJudgeDTO) {
        String openid = userIdStr != null && !userIdStr.isEmpty() ? userIdStr : "test_user_" + System.currentTimeMillis();
        return Result.success(codeQuestionService.judgeCode(openid, codeJudgeDTO));
    }

    @GetMapping("/records/{questionId}")
    public Result<List<UserRecord>> getUserRecords(@RequestHeader(value = "userId", required = false) String userIdStr,
                                                  @PathVariable Integer questionId) {
        String openid = userIdStr != null && !userIdStr.isEmpty() ? userIdStr : "test_user_" + System.currentTimeMillis();
        return Result.success(codeQuestionService.getUserRecords(openid, questionId));
    }

    @GetMapping("/draft/{questionId}")
    public Result<String> getDraft(@RequestHeader(value = "userId", required = false) String userIdStr,
                                  @PathVariable Integer questionId) {
        String openid = userIdStr != null && !userIdStr.isEmpty() ? userIdStr : "test_user_" + System.currentTimeMillis();
        UserRecord record = codeQuestionService.getLatestUserRecord(openid, questionId);
        if (record != null && record.getUserCode() != null) {
            return Result.success(record.getUserCode());
        }
        return Result.success(null);
    }

    @PostMapping("/draft/{questionId}")
    public Result<Void> saveDraft(@RequestHeader(value = "userId", required = false) String userIdStr,
                                 @PathVariable Integer questionId,
                                 @RequestBody Map<String, String> request) {
        String openid = userIdStr != null && !userIdStr.isEmpty() ? userIdStr : "test_user_" + System.currentTimeMillis();
        String userCode = request.get("userCode");
        codeQuestionService.saveDraft(openid, questionId, userCode);
        return Result.success();
    }
}
