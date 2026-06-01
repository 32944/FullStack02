package com.itheima.graduation.controller.admin;

import com.itheima.graduation.dto.UserDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.service.SysUserService;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.UserDetailVO;
import com.itheima.graduation.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserDTO userDTO) {
        sysUserService.add(userDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody UserDTO userDTO) {
        sysUserService.update(userDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @GetMapping("/detail/{id}")
    public Result<UserDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(sysUserService.getUserDetail(id));
    }

    @PostMapping("/page")
    public Result<PageResult<UserVO>> page(@RequestBody UserDTO userDTO) {
        return Result.success(sysUserService.page(userDTO));
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/level")
    public Result<Void> updateLevel(@RequestParam Long id, @RequestParam Integer currentLevel) {
        sysUserService.updateLevel(id, currentLevel);
        return Result.success();
    }
}
