package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Long countByCategoryId(Long id);

    /**
     * 根据菜品 id 查询对应套餐 ids
     */
    List<Long> getSetMealIdsByDishIds(@Param("dishIds") List<Long> dishIds);

}
