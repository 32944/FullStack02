package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.LearningPath;
import com.itheima.graduation.mapper.LearningPathMapper;
import com.itheima.graduation.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningPathServiceImpl implements LearningPathService {

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Override
    public List<LearningPath> getAllPaths() {
        return learningPathMapper.selectAll();
    }

    @Override
    public List<LearningPath> getPathsByCategory(String category) {
        return learningPathMapper.selectByCategory(category);
    }

    @Override
    public LearningPath getPathById(Long id) {
        return learningPathMapper.selectById(id);
    }

    @Override
    public List<String> getAllCategories() {
        return learningPathMapper.selectCategories();
    }
}