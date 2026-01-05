package com.sky.mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据用户和订单号查询订单
     * @param number
     * @param userId
     * @return
     */
    Orders getByNumberAndUserId(@Param("number")String number,@Param("userId")Long userId);

    /**
     * 更新订单
     * @param updated
     */
    void update(@Param("updated")Orders updated);


    Orders getById(@Param("id")Long id);




}