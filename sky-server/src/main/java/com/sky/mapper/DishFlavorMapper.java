package com.sky.mapper;
import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {

    int insertList(@Param("list")List<DishFlavor> list);

    int deleteByDishIds(@Param("dishIds")List<Long> dishId);

}