package com.itheima.graduation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QwenApiUtils {

    private static final String API_KEY = "sk-c7604bb0ad6640eb84dec591c42ce0b0";
    private static final String BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<ObjectNode> parseAndClassifyProblems(String text, List<String> categories) {
        List<ObjectNode> resultList = new ArrayList<>();
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

            String categoriesStr = String.join("、", categories);

            String prompt = "请分析以下文本，提取出所有题目，并为每道题目进行分类和难度评估。\n\n" +
                    "文本内容：\n" + text + "\n\n" +
                    "可选分类：" + categoriesStr + "\n\n" +
                    "要求：\n" +
                    "1. 从文本中提取每道题目的完整内容作为title\n" +
                    "2. 根据题目内容判断最匹配的分类（必须从可选分类中选择）\n" +
                    "3. 评估难度等级（1-5，1最简单，5最难）\n" +
                    "4. 返回JSON数组格式，每个元素包含：title（题目内容）、categoryName（分类名称）、difficulty（难度）\n" +
                    "5. 只返回JSON数组，不要包含其他文字说明。\n\n" +
                    "示例输出格式：\n" +
                    "[\n  {\"title\": \"题目内容\", \"categoryName\": \"分类名称\", \"difficulty\": 3}\n]";

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
                return resultList;
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

                content = content.trim();
                if (content.startsWith("```json")) {
                    content = content.substring(7);
                }
                if (content.startsWith("```")) {
                    content = content.substring(3);
                }
                if (content.endsWith("```")) {
                    content = content.substring(0, content.length() - 3);
                }
                content = content.trim();

                try {
                    JsonNode problems = objectMapper.readTree(content);
                    if (problems.isArray()) {
                        for (JsonNode problem : problems) {
                            if (problem.isObject()) {
                                resultList.add((ObjectNode) problem);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        return resultList;
    }
}
