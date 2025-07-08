package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.Login;
import com.example.demo.pojo.LoginResponse;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;


@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody Login login){
        try {
            User user=userService.validateUser(login.getUsername(),login.getPassword());
            if(user!=null){
                String token=jwtUtil.generateToken(user.getUsername(),user.getRole());
                LoginResponse loginResponse=new LoginResponse(user.getUsername(),token,user.getRole());
                return Result.success("登陆成功",loginResponse);
            }else {
                return Result.error("用户名或密码错误");
            }
        }catch(Exception e){
            return Result.error("登录失败"+e.getMessage());
        }
    }
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user){
        try {
            Boolean success=userService.registerUser(user);
            if(success){
                return Result.success("注册成功","用户创建完成");
            }else{
                return Result.error("注册失败,用户可能已存在");
            }
        }catch (Exception e){
                return Result.error("注册失败"+e.getMessage());
        }
    }
}
