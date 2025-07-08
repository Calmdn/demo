package com.example.demo.interceptor;

import com.example.demo.context.UserContext;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    /*请求中获取token*/
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 从请求参数获取
        String paramToken = request.getParameter("token");
        if (StringUtils.hasText(paramToken)) {
            return paramToken;
        }

        return null;
    }

    /**
     * 发送错误
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("code", status);
        result.put("data", null);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        System.out.println("Jwt拦截器：拦截请求" + request.getRequestURI());

        //请求头获取token
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            System.out.println("Jwt验证器：未找到token");
            sendErrorResponse(response, 401, "未登录，请先登录");
            return false;
        }
        try {
            if (!jwtUtil.validateToken(token)) {
                System.out.println("Jwt拦截器：token 验证失败");
                sendErrorResponse(response, 401, "token无效或已过期");
                return false;
            }
            String username = jwtUtil.getUsernameFromToken(token);
            Integer role = jwtUtil.getRoleFromToken(token);
            System.out.println("Jwt拦截器：token验证成功，用户：" + username + ",角色：" + role);
            //查询完整信息
            User user = userService.findByUsername(username);
            if (user == null) {
                System.out.println("JWT：用户不存在");
                sendErrorResponse(response, 401, "用户不存在");
                return false;
            }
            //信息存入ThreadLocal
            UserContext.setCurrentUser(user);
            return true;
        } catch (Exception e) {
            System.out.println("JWT:处理异常" + e.getMessage());
            sendErrorResponse(response, 401, "身份验证失败");
            return false;
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex)throws Exception{
        UserContext.clear();
        System.out.println("JWT:清理ThreadLoad");
    }
}
