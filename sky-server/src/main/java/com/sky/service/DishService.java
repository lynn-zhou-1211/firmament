package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {

     /**
      * 保存菜品
      * @param dishDTO 存储对象
      */
     void saveWithFlavor(DishDTO dishDTO);

     /**
      * 菜品分页查询
      * @param dishPageQueryDTO 查询对象
      * @return PageResult
      */
     PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

     /**
      * 批量删除菜品
      * @param ids 菜品 id 列表
      */
     void deleteBatch(List<Long> ids) ;
}
