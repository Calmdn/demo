package com.example.demo.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private Integer id;
    private String username;
    private String password;
    private Integer role;
    private Date createTime;
    private Date updateTime;
}
