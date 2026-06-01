package com.itheima.graduation.service;

import com.itheima.graduation.dto.ProblemDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.vo.ProblemVO;

import java.util.List;

public interface ProblemService {

    void add(ProblemDTO problemDTO);

    void update(ProblemDTO problemDTO);

    void delete(Long id);

    ProblemVO getById(Long id);

    PageResult<ProblemVO> page(ProblemDTO problemDTO);

    void updateStatus(Long id, Integer status);

    void batchAdd(String text);
}
