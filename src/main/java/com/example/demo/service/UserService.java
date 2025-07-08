package com.example.demo.service;

import com.example.demo.pojo.User;

public interface UserService {
    User validateUser(String username,String password);
    User findByUsername(String username);
    boolean registerUser(User user);
}
