package com.sky.mapper;
import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.Orders;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

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

    /**
     * 根据 id 查询订单
     * @param id
     * @return
     */
    Orders getById(@Param("id")Long id);


    /**
     * 动态条件查询订单，并按照时间降序排序
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);



}