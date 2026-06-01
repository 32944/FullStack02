package com.itheima.graduation.service;

import com.itheima.graduation.entity.LearningPath;

import java.util.List;

public interface LearningPathService {

    List<LearningPath> getAllPaths();

    List<LearningPath> getPathsByCategory(String category);

    LearningPath getPathById(Long id);

    List<String> getAllCategories();
}