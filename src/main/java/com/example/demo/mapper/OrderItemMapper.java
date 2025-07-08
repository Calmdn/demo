package com.example.demo.mapper;

import com.example.demo.pojo.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    // 批量插入订单项
    void batchInsert(List<OrderItem> orderItems);

    // 根据订单ID查询订单项
    List<OrderItem> selectByOrderId(Integer orderId);

    // 查询卖家的销售记录
    List<OrderItem> selectBySellerId(@Param("sellerId") Integer sellerId,
                                     @Param("status") Integer status,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);
}