package com.itheima.graduation.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itheima.graduation.dto.ProblemDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.entity.Problem;
import com.itheima.graduation.entity.ProblemCategory;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.ProblemMapper;
import com.itheima.graduation.service.ProblemCategoryService;
import com.itheima.graduation.service.ProblemService;
import com.itheima.graduation.utils.QwenApiUtils;
import com.itheima.graduation.vo.ProblemVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemCategoryService problemCategoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @CacheEvict(value = "problem", allEntries = true)
    public void add(ProblemDTO problemDTO) {
        if (problemDTO.getTitle() == null || problemDTO.getTitle().trim().isEmpty()) {
            throw new BusinessException("题目标题不能为空");
        }
        if (problemDTO.getCategoryId() == null) {
            throw new BusinessException("题目分类不能为空");
        }
        if (problemDTO.getDescription() == null || problemDTO.getDescription().trim().isEmpty()) {
            throw new BusinessException("题目描述不能为空");
        }

        Problem problem = new Problem();
        BeanUtils.copyProperties(problemDTO, problem);

        if (problem.getDifficulty() == null) {
            problem.setDifficulty(1);
        }
        if (problem.getStatus() == null) {
            problem.setStatus(1);
        }

        problemMapper.insert(problem);
    }

    @Override
    @CacheEvict(value = "problem", allEntries = true)
    public void update(ProblemDTO problemDTO) {
        if (problemDTO.getId() == null) {
            throw new BusinessException("题目ID不能为空");
        }

        String lockKey = "problem:update:" + problemDTO.getId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }

            Problem existProblem = problemMapper.selectById(problemDTO.getId());
            if (existProblem == null) {
                throw new BusinessException("题目不存在");
            }

            Problem problem = new Problem();
            BeanUtils.copyProperties(problemDTO, problem);
            problemMapper.updateById(problem);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @CacheEvict(value = "problem", allEntries = true)
    public void delete(Long id) {
        String lockKey = "problem:delete:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            problemMapper.deleteById(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Cacheable(value = "problem", key = "'getById:' + #id")
    public ProblemVO getById(Long id) {
        Problem problem = problemMapper.selectById(id);
        if (problem == null) {
            throw new BusinessException("题目不存在");
        }
        return convertToVO(problem);
    }

    @Override
    public PageResult<ProblemVO> page(ProblemDTO problemDTO) {
        Integer offset = (problemDTO.getPageNum() - 1) * problemDTO.getPageSize();

        List<Problem> problemList = problemMapper.selectPage(
                problemDTO.getTitle(),
                problemDTO.getCategoryId(),
                problemDTO.getStatus(),
                offset,
                problemDTO.getPageSize()
        );

        int total = problemMapper.count(
                problemDTO.getTitle(),
                problemDTO.getCategoryId(),
                problemDTO.getStatus()
        );

        List<ProblemVO> voList = new ArrayList<>();
        List<ProblemCategory> allCategories = problemCategoryService.getAll();
        Map<Long, String> categoryNameMap = new HashMap<>();
        for (ProblemCategory category : allCategories) {
            categoryNameMap.put(category.getId(), category.getName());
        }

        for (Problem problem : problemList) {
            ProblemVO vo = convertToVO(problem);
            vo.setCategoryName(categoryNameMap.get(problem.getCategoryId()));
            voList.add(vo);
        }

        return new PageResult<>((long) total, voList);
    }

    @Override
    @CacheEvict(value = "problem", allEntries = true)
    public void updateStatus(Long id, Integer status) {
        String lockKey = "problem:status:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            problemMapper.updateStatus(id, status);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @CacheEvict(value = "problem", allEntries = true)
    public void batchAdd(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new BusinessException("题目文本不能为空");
        }

        String lockKey = "problem:batchAdd";
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 60, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }

            List<ProblemCategory> allCategories = problemCategoryService.getAll();
            if (allCategories == null || allCategories.isEmpty()) {
                throw new BusinessException("题目分类为空，请先添加分类");
            }

            Map<String, Long> categoryNameToIdMap = new HashMap<>();
            List<String> categoryNames = new ArrayList<>();
            for (ProblemCategory category : allCategories) {
                categoryNameToIdMap.put(category.getName(), category.getId());
                categoryNames.add(category.getName());
            }

            List<ObjectNode> aiResults = QwenApiUtils.parseAndClassifyProblems(text, categoryNames);
            if (aiResults == null || aiResults.isEmpty()) {
                throw new BusinessException("未能从文本中解析出题目");
            }

            List<Problem> problems = new ArrayList<>();
            for (ObjectNode result : aiResults) {
                String title = result.has("title") ? result.get("title").asText("") : "";
                String categoryName = result.has("categoryName") ? result.get("categoryName").asText("") : "";
                Integer difficulty = result.has("difficulty") ? result.get("difficulty").asInt(1) : 1;

                if (title == null || title.trim().isEmpty()) {
                    continue;
                }

                Problem problem = new Problem();
                problem.setTitle(title);

                if (categoryName != null && !categoryName.isEmpty() && categoryNameToIdMap.containsKey(categoryName)) {
                    problem.setCategoryId(categoryNameToIdMap.get(categoryName));
                } else {
                    problem.setCategoryId(allCategories.get(0).getId());
                }

                problem.setDifficulty(Math.min(Math.max(difficulty, 1), 5));
                problem.setDescription("");
                problem.setStatus(1);

                problems.add(problem);
            }

            if (!problems.isEmpty()) {
                problemMapper.insertBatch(problems);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private ProblemVO convertToVO(Problem problem) {
        ProblemVO problemVO = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVO);
        return problemVO;
    }
}
