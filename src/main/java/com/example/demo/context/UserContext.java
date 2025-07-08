package com.example.demo.context;

import com.example.demo.pojo.User;
import jakarta.persistence.criteria.CriteriaBuilder;

public class UserContext {
    private static final ThreadLocal<User> userThreadLocal=new ThreadLocal<>();

//    设置当前用户
    public static void setCurrentUser(User user){
        userThreadLocal.set(user);
    }
//    获取当前用户
    public static User getCurrentUser(){
        return userThreadLocal.get();
    }
//获取当然用户ID
    public static Integer getCurrentUserId(){
        User user=getCurrentUser();
        return user!=null?user.getId():null;
    }
//    当然用户名
    public static String getCurrentUsername(){
        User user=getCurrentUser();
        return user!=null?user.getUsername():null;
    }
//    用户角色
    public static Integer getCurrentUserRole(){
        User user=getCurrentUser();
        return user!=null?user.getRole():null;
    }
//    清除
    public static void clear(){
        userThreadLocal.remove();
    }
}
