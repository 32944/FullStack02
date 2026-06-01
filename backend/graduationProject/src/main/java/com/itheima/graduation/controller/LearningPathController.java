package com.itheima.graduation.controller;

import com.itheima.graduation.entity.LearningPath;
import com.itheima.graduation.service.LearningPathService;
import com.itheima.graduation.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learning")
public class LearningPathController {

    @Autowired
    private LearningPathService learningPathService;

    @GetMapping("/paths")
    public Result<List<LearningPath>> getPaths(@RequestParam(required = false) String category) {
        List<LearningPath> paths;
        if (category != null && !category.isEmpty()) {
            paths = learningPathService.getPathsByCategory(category);
        } else {
            paths = learningPathService.getAllPaths();
        }
        return Result.success(paths);
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = learningPathService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/path/{id}")
    public Result<LearningPath> getPathById(@PathVariable Long id) {
        LearningPath path = learningPathService.getPathById(id);
        return Result.success(path);
    }

    @GetMapping("/article/{id}")
    public Result<Map<String, Object>> getArticleContent(@PathVariable Long id) {
        LearningPath path = learningPathService.getPathById(id);
        if (path == null) {
            return Result.error("文章不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("title", path.getTitle());
        result.put("target", path.getTarget());
        
        String rawContent = path.getContent();
        String formattedContent = formatContent(rawContent);
        result.put("content", formattedContent);
        
        return Result.success(result);
    }
    
    private String formatContent(String rawContent) {
        if (rawContent == null || rawContent.isEmpty()) {
            return "<p>暂无内容</p>";
        }
        
        StringBuilder sb = new StringBuilder();
        String[] sentences = rawContent.split("(?<=[。！？.!?])");
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                sb.append("<p>").append(sentence).append("</p>");
            }
        }
        
        if (sb.length() == 0) {
            String[] lines = rawContent.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    sb.append("<p>").append(line).append("</p>");
                }
            }
        }
        
        if (sb.length() == 0) {
            int maxLen = 100;
            for (int i = 0; i < rawContent.length(); i += maxLen) {
                int end = Math.min(i + maxLen, rawContent.length());
                sb.append("<p>").append(rawContent.substring(i, end)).append("</p>");
            }
        }
        
        return sb.toString();
    }
}
