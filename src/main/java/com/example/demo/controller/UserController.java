package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 获取当前用户信息（需要登录）
     */
    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        User currentUser = UserContext.getCurrentUser();
        return Result.success("获取用户信息成功", currentUser);
    }

    /**
     * 测试管理员权限（需要登录且为管理员）
     */
    @GetMapping("/admin-test")
    public Result<String> adminTest() {
        User currentUser = UserContext.getCurrentUser();

        if (currentUser.getRole() != 1) {
            return Result.error("权限不足，需要管理员权限",403);
        }

        return Result.success("管理员权限验证成功", "欢迎管理员：" + currentUser.getUsername());
    }
}