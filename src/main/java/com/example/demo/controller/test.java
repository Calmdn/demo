package com.example.demo.controller;

import com.example.demo.context.UserContext;
import com.example.demo.pojo.CreateOrderRequest;
import com.example.demo.pojo.User;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test")
public class test {

    @Autowired
    private OrderService orderService;

    @PostMapping("/concurrent-purchase")
    public Map<String, Object> testConcurrentPurchase(@RequestParam Integer artworkId) {
        Map<String, Object> result = new HashMap<>();
        List<String> results = new ArrayList<>();

        // 模拟10个并发请求
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // 🔑 创建User对象并设置到上下文
                    Integer userId = 4 + (threadNum % 6); // 使用user4-user9
                    User user = new User();
                    user.setId(userId);
                    user.setUsername("user" + userId);
                    user.setRole(0); // 普通用户

                    UserContext.setCurrentUser(user); // 设置完整的User对象

                    CreateOrderRequest request = new CreateOrderRequest();
                    request.setArtworkIds(Arrays.asList(artworkId));
                    request.setPaymentMethod("alipay");
                    request.setRemark("并发测试-线程" + threadNum + "-用户" + userId);

                    String orderNo = orderService.createOrder(request);
                    results.add("线程" + threadNum + "(用户" + userId + "): 成功创建订单 " + orderNo);

                } catch (Exception e) {
                    Integer userId = UserContext.getCurrentUserId();
                    results.add("线程" + threadNum + "(用户" + userId + "): 失败 - " + e.getMessage());
                } finally {
                    UserContext.clear(); // 清理ThreadLocal
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();

        result.put("results", results);
        return result;
    }
}