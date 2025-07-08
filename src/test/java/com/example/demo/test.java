package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test {
    // 可以写个临时方法生成正确的BCrypt密码
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("123456");
        System.out.println("123456的BCrypt密码: " + encoded);
    }
}
