package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.LearningPath;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningPathMapper {

    List<LearningPath> selectAll();

    List<LearningPath> selectByCategory(String category);

    LearningPath selectById(Long id);

    List<String> selectCategories();
}