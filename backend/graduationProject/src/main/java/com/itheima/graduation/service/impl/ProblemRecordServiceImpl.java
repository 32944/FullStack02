package com.itheima.graduation.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.ProblemRecord;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.mapper.ProblemRecordMapper;
import com.itheima.graduation.service.ProblemRecordService;
import com.itheima.graduation.vo.ProblemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ProblemRecordServiceImpl implements ProblemRecordService {

    private static final String API_KEY = "sk-c7604bb0ad6640eb84dec591c42ce0b0";
    private static final String BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProblemRecordMapper problemRecordMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public Integer submitAnswer(Long userId, Long problemId, String answer) {
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }

        int status = checkAnswerWithQwen(problem.getTitle(), problem.getDescription(), answer);

        ProblemRecord existingRecord = problemRecordMapper.selectByUserIdAndProblemId(userId, problemId);
        if (existingRecord != null) {
            existingRecord.setAnsewr(answer);
            existingRecord.setStatus(status);
            problemRecordMapper.update(existingRecord);
        } else {
            ProblemRecord record = new ProblemRecord();
            record.setUserId(userId);
            record.setProblemId(problemId);
            record.setAnsewr(answer);
            record.setStatus(status);
            problemRecordMapper.insert(record);
        }

        return status;
    }

    @Override
    public List<ProblemVO> getWrongProblems(Long userId) {
        List<ProblemVO> result = new ArrayList<>();
        
        List<Long> problemIds = problemRecordMapper.selectProblemIdsByUserAndStatus(userId, 0);
        
        for (Long problemId : problemIds) {
            Problem problem = problemMapper.selectById(problemId);
            if (problem != null && problem.getStatus() == 1) {
                ProblemVO vo = new ProblemVO();
                BeanUtils.copyProperties(problem, vo);
                result.add(vo);
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        int totalProblems = problemMapper.count(null, null, 1);
        
        List<ProblemRecord> allRecords = problemRecordMapper.selectByUserId(userId);
        
        int totalAnswered = allRecords.size();
        int wrongCount = 0;
        int correctCount = 0;
        
        for (ProblemRecord record : allRecords) {
            if (record.getStatus() != null) {
                if (record.getStatus() == 1) {
                    correctCount++;
                } else {
                    wrongCount++;
                }
            }
        }
        
        double accuracy = 0;
        if (totalAnswered > 0) {
            accuracy = (double) correctCount / totalAnswered * 100;
        }
        
        stats.put("totalProblems", totalProblems);
        stats.put("totalAnswered", totalAnswered);
        stats.put("wrongCount", wrongCount);
        stats.put("correctCount", correctCount);
        stats.put("accuracy", String.format("%.1f%%", accuracy));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getUserExerciseRecords(Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<ProblemRecord> records = problemRecordMapper.selectByUserIdOrderByTime(userId);
        
        Map<String, List<Map<String, Object>>> groupedByDate = new LinkedHashMap<>();
        
        for (ProblemRecord record : records) {
            String dateStr = formatDate(record.getCreateTime());
            
            Problem problem = problemMapper.selectById(record.getProblemId());
            
            Map<String, Object> problemInfo = new HashMap<>();
            problemInfo.put("recordId", record.getId());
            problemInfo.put("problemId", record.getProblemId());
            problemInfo.put("title", problem != null ? problem.getTitle() : "未知题目");
            problemInfo.put("status", record.getStatus());
            problemInfo.put("answer", record.getAnsewr());
            problemInfo.put("createTime", record.getCreateTime());
            
            groupedByDate.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(problemInfo);
        }
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedByDate.entrySet()) {
            Map<String, Object> dayRecord = new HashMap<>();
            dayRecord.put("date", entry.getKey());
            dayRecord.put("count", entry.getValue().size());
            dayRecord.put("problems", entry.getValue());
            result.add(dayRecord);
        }
        
        return result;
    }
    
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private int checkAnswerWithQwen(String problemTitle, String problemDesc, String userAnswer) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        OutputStream os = null;

        try {
            URL url = new URL(BASE_URL + "/chat/completions");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            String prompt = buildCheckPrompt(problemTitle, problemDesc, userAnswer);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "qwen3.6-plus");
            requestBody.put("temperature", 0.3);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.set("messages", messages);

            os = conn.getOutputStream();
            os.write(objectMapper.writeValueAsString(requestBody).getBytes(StandardCharsets.UTF_8));
            os.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("HTTP请求失败，状态码: " + responseCode);
                return 0;
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JsonNode responseJson = objectMapper.readTree(response.toString());
            JsonNode choices = responseJson.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode choice = choices.get(0);
                JsonNode msg = choice.get("message");
                String content = msg.get("content").asText();
                return parseStatusFromResponse(content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (os != null) {
                    os.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private String buildCheckPrompt(String problemTitle, String problemDesc, String userAnswer) {
        StringBuilder sb = new StringBuilder();
        sb.append("请判断以下答案是否正确。\n\n");
        sb.append("题目：").append(problemTitle).append("\n\n");
        if (problemDesc != null && !problemDesc.trim().isEmpty()) {
            sb.append("题目描述：").append(problemDesc).append("\n\n");
        }
        sb.append("用户答案：").append(userAnswer).append("\n\n");
        sb.append("要求：\n");
        sb.append("1. 判断答案是否正确\n");
        sb.append("2. 只返回数字，正确返回1，错误返回0\n");
        sb.append("3. 不要包含其他任何文字说明\n");
        sb.append("示例输出：1");
        return sb.toString();
    }

    private int parseStatusFromResponse(String content) {
        content = content.trim();
        try {
            int status = Integer.parseInt(content);
            return (status == 1) ? 1 : 0;
        } catch (NumberFormatException e) {
            if (content.contains("正确") || content.contains("对") || content.contains("pass")) {
                return 1;
            }
            return 0;
        }
    }
}
