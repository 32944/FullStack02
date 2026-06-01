package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.ProblemCategory;
import com.itheima.graduation.entity.ProblemRecord;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.mapper.ProblemRecordMapper;
import com.itheima.graduation.service.ProblemCategoryService;
import com.itheima.graduation.service.TrainingService;
import com.itheima.graduation.vo.TrainingCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private ProblemCategoryService problemCategoryService;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemRecordMapper problemRecordMapper;

    @Override
    public List<TrainingCategoryVO> getUserTrainingCategories(Long userId) {
        List<ProblemCategory> categories = problemCategoryService.getAll();
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<TrainingCategoryVO> voList = new ArrayList<>();

        List<ProblemRecord> userRecords = problemRecordMapper.selectByUserId(userId);
        
        Map<Long, Integer> categoryCompletedMap = new HashMap<>();
        Map<Long, Integer> categoryCorrectMap = new HashMap<>();
        
        for (ProblemRecord record : userRecords) {
            Problem problem = problemMapper.selectById(record.getProblemId());
            if (problem != null) {
                Long categoryId = problem.getCategoryId();
                categoryCompletedMap.merge(categoryId, 1, Integer::sum);
                if (record.getStatus() != null && record.getStatus() == 1) {
                    categoryCorrectMap.merge(categoryId, 1, Integer::sum);
                }
            }
        }

        for (ProblemCategory category : categories) {
            TrainingCategoryVO vo = new TrainingCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());

            int total = problemMapper.count(null, category.getId(), 1);
            vo.setTotal(total);

            int completed = categoryCompletedMap.getOrDefault(category.getId(), 0);
            int correctCount = categoryCorrectMap.getOrDefault(category.getId(), 0);
            
            vo.setCompleted(completed);
            vo.setCorrectCount(correctCount);
            
            int accuracy = 0;
            if (completed > 0) {
                accuracy = (int) ((double) correctCount / completed * 100);
            }
            vo.setAccuracy(accuracy);
            
            int progress = 0;
            if (total > 0) {
                progress = (int) ((double) completed / total * 100);
            }
            vo.setProgress(progress);
            vo.setLastProgress(progress);

            voList.add(vo);
        }

        return voList;
    }
}
