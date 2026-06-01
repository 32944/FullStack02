package com.itheima.graduation.controller;

import com.itheima.graduation.utils.Result;
import com.itheima.graduation.vo.WxLoginVO;
import com.itheima.graduation.service.WxLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/wx")
public class WxLoginController {

    @Autowired
    private WxLoginService wxLoginService;
    
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/avatars/";

    @PostMapping("/login")
    public Result<WxLoginVO> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickname = params.get("nickname");
        String avatar = params.get("avatar");
        
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }
        
        WxLoginVO result = wxLoginService.login(code, nickname, avatar);
        return Result.success(result);
    }
    
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择图片");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(UPLOAD_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String fileName = UUID.randomUUID().toString() + suffix;
            
            // 保存文件
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);
            
            // 返回访问路径
            String avatarUrl = "/uploads/avatars/" + fileName;
            return Result.success(avatarUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
