package com.itheima.graduation.controller.admin;

import com.itheima.graduation.dto.ProblemDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.entity.ProblemCategory;
import com.itheima.graduation.service.ProblemCategoryService;
import com.itheima.graduation.service.ProblemService;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.ProblemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemCategoryService problemCategoryService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ProblemDTO problemDTO) {
        problemService.add(problemDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ProblemDTO problemDTO) {
        problemService.update(problemDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        problemService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<ProblemVO> getById(@PathVariable Long id) {
        return Result.success(problemService.getById(id));
    }

    @PostMapping("/page")
    public Result<PageResult<ProblemVO>> page(@RequestBody ProblemDTO problemDTO) {
        return Result.success(problemService.page(problemDTO));
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        problemService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/batchAdd")
    public Result<Void> batchAdd(@RequestParam("text") String text) {
        problemService.batchAdd(text);
        return Result.success();
    }

    @GetMapping("/category/list")
    public Result<List<ProblemCategory>> categoryList() {
        return Result.success(problemCategoryService.getAll());
    }
}
