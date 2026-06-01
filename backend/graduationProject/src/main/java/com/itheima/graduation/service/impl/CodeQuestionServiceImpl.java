package com.itheima.graduation.service.impl;

import com.itheima.graduation.dto.CodeJudgeDTO;
import com.itheima.graduation.entity.CodeQuestion;
import com.itheima.graduation.entity.TestCase;
import com.itheima.graduation.entity.UserRecord;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.CodeQuestionMapper;
import com.itheima.graduation.service.CodeQuestionService;
import com.itheima.graduation.vo.CodeJudgeResultVO;
import com.itheima.graduation.vo.CodeQuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CodeQuestionServiceImpl implements CodeQuestionService {

    @Autowired
    private CodeQuestionMapper codeQuestionMapper;

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/code_judge";

    @Override
    public CodeQuestionVO getById(Long id) {
        CodeQuestion question = codeQuestionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }
        List<TestCase> testCases = codeQuestionMapper.selectTestCasesByQuestionId(id.intValue());

        CodeQuestionVO vo = new CodeQuestionVO();
        BeanUtils.copyProperties(question, vo);

        List<CodeQuestionVO.TestCaseVO> caseVOList = new ArrayList<>();
        for (TestCase tc : testCases) {
            CodeQuestionVO.TestCaseVO tcVO = new CodeQuestionVO.TestCaseVO();
            tcVO.setId(tc.getId());
            tcVO.setStdin(tc.getStdin());
            tcVO.setStdout(tc.getStdout());
            tcVO.setIsExample(tc.getIsExample());
            caseVOList.add(tcVO);
        }
        vo.setTestCases(caseVOList);

        return vo;
    }

    @Override
    public List<CodeQuestion> getAllQuestions() {
        return codeQuestionMapper.selectAll();
    }

    @Override
    public CodeJudgeResultVO judgeCode(String openid, CodeJudgeDTO codeJudgeDTO) {
        long startTime = System.currentTimeMillis();
        CodeJudgeResultVO result = new CodeJudgeResultVO();

        Integer questionId = codeJudgeDTO.getQuestionId();
        String userCode = codeJudgeDTO.getUserCode();

        if (userCode == null || userCode.trim().isEmpty()) {
            throw new BusinessException("代码不能为空");
        }

        List<TestCase> testCases = codeQuestionMapper.selectTestCasesByQuestionId(questionId);
        if (testCases == null || testCases.isEmpty()) {
            throw new BusinessException("题目没有测试用例");
        }

        int passedCount = 0;
        String runOutput = "";
        String finalResult = "Accepted";
        String message = "通过所有测试用例";

        try {
            Path tempDir = Paths.get(TEMP_DIR, UUID.randomUUID().toString());
            Files.createDirectories(tempDir);

            Path javaFile = tempDir.resolve("Main.java");
            Files.write(javaFile, userCode.getBytes(StandardCharsets.UTF_8));

            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(tempDir.toFile());
            compilePb.redirectErrorStream(true);
            Process compileProcess = compilePb.start();

            StringBuilder compileOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(compileProcess.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    compileOutput.append(line).append("\n");
                }
            }

            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                finalResult = "Compile Error";
                message = compileOutput.toString();
                result.setResult(finalResult);
                result.setMessage(message);
                result.setPassedCount(0);
                result.setTotalCount(testCases.size());
                result.setTimeMs(System.currentTimeMillis() - startTime);
                saveRecord(openid, questionId, userCode, runOutput, finalResult);
                return result;
            }

            for (TestCase testCase : testCases) {
                String stdin = testCase.getStdin();
                String expected = testCase.getStdout().trim();

                List<String> inputLines = parseInput(stdin);
                String actual = runProgram(tempDir, "Main", inputLines);
                runOutput = actual;

                if (actual.trim().equals(expected)) {
                    passedCount++;
                } else {
                    finalResult = "Wrong Answer";
                    message = "测试用例失败: 期望 \"" + expected + "\", 实际 \"" + actual + "\"";
                    break;
                }
            }

            deleteDirectory(tempDir.toFile());

        } catch (Exception e) {
            finalResult = "Runtime Error";
            message = e.getMessage();
            e.printStackTrace();
        }

        result.setResult(finalResult);
        result.setMessage(message);
        result.setPassedCount(passedCount);
        result.setTotalCount(testCases.size());
        result.setTimeMs(System.currentTimeMillis() - startTime);

        saveRecord(openid, questionId, userCode, runOutput, finalResult);

        return result;
    }

    @Override
    public List<UserRecord> getUserRecords(String openid, Integer questionId) {
        return codeQuestionMapper.selectUserRecordsByOpenidAndQuestionId(openid, questionId);
    }

    private List<String> parseInput(String stdin) {
        List<String> lines = new ArrayList<>();
        if (stdin != null) {
            String[] parts = stdin.split("\\n");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    lines.add(part.trim());
                }
            }
        }
        return lines;
    }

    private String runProgram(Path tempDir, String className, List<String> inputLines) throws Exception {
        ProcessBuilder runPb = new ProcessBuilder("java", className);
        runPb.directory(tempDir.toFile());
        runPb.redirectErrorStream(true);
        Process runProcess = runPb.start();

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(runProcess.getOutputStream(), StandardCharsets.UTF_8))) {
            for (String line : inputLines) {
                writer.println(line);
            }
            writer.flush();
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int runExitCode = runProcess.waitFor();
        if (runExitCode != 0) {
            throw new RuntimeException("程序执行失败");
        }

        return output.toString().trim();
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    private void saveRecord(String openid, Integer questionId, String userCode, String runOutput, String result) {
        UserRecord record = new UserRecord();
        record.setOpenid(openid);
        record.setQuestionId(questionId);
        record.setUserCode(userCode);
        record.setRunOutput(runOutput);
        record.setResult(result);
        codeQuestionMapper.insertUserRecord(record);
    }

    @Override
    public UserRecord getLatestUserRecord(String openid, Integer questionId) {
        return codeQuestionMapper.selectLatestUserRecord(openid, questionId);
    }

    @Override
    public void saveDraft(String openid, Integer questionId, String userCode) {
        UserRecord record = new UserRecord();
        record.setOpenid(openid);
        record.setQuestionId(questionId);
        record.setUserCode(userCode);
        record.setResult("draft");
        codeQuestionMapper.insertUserRecord(record);
    }
}
