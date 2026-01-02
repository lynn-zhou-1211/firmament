package com.sky.mapper;
import java.util.List;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface DishMapper {

    Long countByCategoryId(@Param("categoryId")Long categoryId);

    @AutoFill(OperationType.INSERT)
    int insert(Dish dish);





}