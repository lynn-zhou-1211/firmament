package com.sky.mapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {
    int insertSelective(SetmealDish setmealDish);

    int insertBatch(List<SetmealDish> list);

}
