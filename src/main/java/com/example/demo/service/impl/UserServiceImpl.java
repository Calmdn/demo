package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User validateUser(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword()))
                return user;
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean registerUser(User user) {
        try {
            if (findByUsername(user.getUsername()) != null) {
                return false;
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getRole() == null) {
                user.setRole(0);
            }
            return userMapper.insertUser(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
