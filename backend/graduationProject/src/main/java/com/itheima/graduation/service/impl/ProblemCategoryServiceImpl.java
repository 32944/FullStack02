package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.ProblemCategory;
import com.itheima.graduation.mapper.ProblemCategoryMapper;
import com.itheima.graduation.service.ProblemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemCategoryServiceImpl implements ProblemCategoryService {

    @Autowired
    private ProblemCategoryMapper problemCategoryMapper;

    @Override
    @Cacheable(value = "category", key = "'all'")
    public List<ProblemCategory> getAll() {
        return problemCategoryMapper.selectAll();
    }
}
