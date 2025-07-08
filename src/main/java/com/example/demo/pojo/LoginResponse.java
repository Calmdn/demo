package com.example.demo.pojo;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private Integer role;
    private String roleName;

    public LoginResponse(String username,String token,Integer role){
        this.role=role;
        this.token=token;
        this.username=username;
        this.roleName=role==1?"管理员" :"普通用户";
    }
}
