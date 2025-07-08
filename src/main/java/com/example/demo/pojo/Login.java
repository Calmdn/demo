package com.example.demo.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Login {
    private String username;
    private String password;
    private String captcha;
    private Boolean rememberMe;
}
