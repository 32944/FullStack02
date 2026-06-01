package com.itheima.graduation.service;

import com.itheima.graduation.dto.CodeJudgeDTO;
import com.itheima.graduation.entity.CodeQuestion;
import com.itheima.graduation.entity.UserRecord;
import com.itheima.graduation.vo.CodeJudgeResultVO;
import com.itheima.graduation.vo.CodeQuestionVO;

import java.util.List;

public interface CodeQuestionService {

    CodeQuestionVO getById(Long id);

    List<CodeQuestion> getAllQuestions();

    CodeJudgeResultVO judgeCode(String openid, CodeJudgeDTO codeJudgeDTO);

    List<UserRecord> getUserRecords(String openid, Integer questionId);

    UserRecord getLatestUserRecord(String openid, Integer questionId);

    void saveDraft(String openid, Integer questionId, String userCode);
}
