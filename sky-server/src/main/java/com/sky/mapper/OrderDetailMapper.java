package com.sky.mapper;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入 orderDetails
     * @param list
     * @return
     */
    int insertList(@Param("list")List<OrderDetail> list);


}