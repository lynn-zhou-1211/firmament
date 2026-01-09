package com.sky.mapper;
import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.Orders;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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


    /**
     * 根据状态和下单时间查询订单
     * @param status 订单状态
     * @param orderTime 下单时间
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(@Param("status") Integer status,@Param("orderTime") LocalDateTime orderTime);

    /**
     * 根据动态条件统计营业额
     * @param map 包含 beginTime, endTime, status
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     */
    Integer countByMap(Map map);

    /**
     * 查询销量排名Top10
     * @param map
     */
    List<GoodsSalesDTO> getSalesTop10(Map map);
}