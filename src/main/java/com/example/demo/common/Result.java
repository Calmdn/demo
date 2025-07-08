package com.example.demo.common;

import lombok.Data;

@Data
public class Result<T> {
    private Boolean success;
    private String message;
    private T data;
    private Integer code;

    public static <T> Result<T> success(T data) {
        Result<T> result =new Result<>();
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setData(data);
        result.setCode(200);
        return result;
    }
    public static <T> Result<T> success(String message,T data){
        Result<T> result=new Result<>();
        result.setCode(200);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(true);
        return result;
    }
    public static <T> Result<T> error(String message){
        Result<T> result=new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCode(400);
        return result;
    }
    public static <T> Result<T> error(String message,Integer code){
        Result<T> result=new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCode(code);
        return result;
    }
}
