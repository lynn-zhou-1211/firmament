package com.sky.mapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {
    /**
     * 新增套餐-菜品
     * @param setmealDish
     * @return
     */
    int insertSelective(SetmealDish setmealDish);

    /**
     * 批量插入套餐-菜品
     * @param list
     * @return
     */
    int insertBatch(List<SetmealDish> list);

    /**
     * 批量删除套餐-菜品
     * @param setMealIds
     */
    void deleteBatch(@Param("setMealIds") List<Long> setMealIds);

    /**
     * 根据套餐id查询套餐-菜品
     * @param setmealId
     * @return
     */
    List<SetmealDish> getBySetmealId(Long setmealId);

}
