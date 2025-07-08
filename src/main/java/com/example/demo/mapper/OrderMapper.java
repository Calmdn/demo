package com.example.demo.mapper;

import com.example.demo.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 插入订单
    void insert(Order order);

    // 根据ID查询订单详情
    Order selectById(Integer id);

    // 根据订单号查询
    Order selectByOrderNo(String orderNo);

    // 查询用户订单列表
    List<Order> selectByUserId(@Param("userId") Integer userId,
                               @Param("status") Integer status,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    // 更新订单状态
    void updateStatus(@Param("orderId") Integer orderId, @Param("status") Integer status);

    /**
     * 计算指定日期的收入
     */
    BigDecimal calculateDailyRevenue(@Param("date") LocalDate date);

    /**
     * 计算指定日期范围的收入
     */
    BigDecimal calculateRevenueByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * 计算用户指定月份的收入
     */
    BigDecimal calculateUserRevenueByMonth(@Param("userId") Integer userId,
                                           @Param("statMonth") String statMonth);

    /**
     * 获取收入趋势数据
     */
    List<Map<String, Object>> getRevenueTrends(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期的订单数量
     */
    int countOrdersByDate(@Param("date") LocalDate date);

    /**
     * 获取热销作品统计
     */
    List<Map<String, Object>> getTopSellingArtworks(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("limit") int limit);

    void update(Order order);

    List<Order> selectTimeoutOrders();

}
