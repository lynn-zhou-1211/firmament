package com.sky.mapper;
import java.util.List;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    Long countByCategoryId(@Param("categoryId")Long categoryId);

    int insert(Dish dish);





}