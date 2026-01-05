package com.sky.mapper;
import java.math.BigDecimal;
import java.util.List;

import com.sky.annotation.AutoFill;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.ShoppingCart;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据【用户id + 菜品/套餐id + 口味】查询购物车条目
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> getByConditions(ShoppingCart shoppingCart);

    /**
     * 更新购物车条目数量
     * @param shoppingCart
     * @return
     */
    void updateNumber(ShoppingCart shoppingCart);

    /**
     * 新增购物车条目
     * @param shoppingCart
     * @return
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据 id 删除条目
     * @param id
     * @return
     */
    void delete(@Param("id")Long id);

    void deleteByUserId(@Param("userId")Long userId);






}