package com.itheima.graduation.controller.admin;

import com.itheima.graduation.dto.LoginDTO;
import com.itheima.graduation.service.SysAdminService;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SysAdminService sysAdminService;
//登录接口#@valid的注解的作用是配合@NotBlank注解用于参数校验
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = sysAdminService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(loginVO);
    }
}
