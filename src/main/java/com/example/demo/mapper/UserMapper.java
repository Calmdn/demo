package com.example.demo.mapper;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    int insertUser(User user);
    int countNewUsersByDate(@Param("date") LocalDate date);
}
