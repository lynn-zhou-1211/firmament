package com.sky.mapper;
import com.sky.entity.Orders;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper {

    /**
     * 插入订单
     * @param orders
     * @return
     */
    int insert(Orders orders);


}