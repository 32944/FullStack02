package com.itheima.graduation.service;

import com.itheima.graduation.vo.TrainingCategoryVO;

import java.util.List;

public interface TrainingService {

    List<TrainingCategoryVO> getUserTrainingCategories(Long userId);
}
