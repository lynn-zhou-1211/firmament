package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper cartMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO cartDTO) {
        // 1. 获取当前用户 id
        Long currentId = BaseContext.getCurrentId();

        // 2. 构建查询条件：用户+菜品+口味
        ShoppingCart cart = ShoppingCart.builder()
                .userId(currentId)
                .dishId(cartDTO.getDishId())
                .setmealId(cartDTO.getSetmealId())
                .dishFlavor(cartDTO.getDishFlavor())
                .build();

        // 3. 查询数据库看当前记录是否存在
        List<ShoppingCart> items = cartMapper.getByConditions(cart);

        // 4. 存在->number++； 不存在->查询菜品/套餐表，number=1
        if (items != null && items.size() == 1) {
            ShoppingCart item = items.get(0);
            item.setNumber(item.getNumber() + 1);
            cartMapper.updateNumber(item);
        } else {
            // 判断是菜品还是套餐
            Long dishId = cartDTO.getDishId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                cart.setName(dish.getName());
                cart.setImage(dish.getImage());
                cart.setAmount(dish.getPrice());
            } else {
                SetmealVO setmeal = setmealMapper.getById(cartDTO.getDishId());
                cart.setName(setmeal.getName());
                cart.setImage(setmeal.getImage());
                cart.setAmount(setmeal.getPrice());
            }
            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
    }

    @Override
    public void sub(ShoppingCartDTO cartDTO) {
        // 1. 构建查询条件：用户+菜品+口味；查询记录
        Long currentId = BaseContext.getCurrentId();

        ShoppingCart cart = ShoppingCart.builder()
                .userId(currentId)
                .dishId(cartDTO.getDishId())
                .setmealId(cartDTO.getSetmealId())
                .dishFlavor(cartDTO.getDishFlavor())
                .build();
        List<ShoppingCart> items = cartMapper.getByConditions(cart);
        if (items != null && items.size() == 1) {
            // 2.判断数量：>1 number-1; ==1, 直接删除记录
            ShoppingCart item = items.get(0);
            int number = item.getNumber();
            if (number > 1) {
                item.setNumber(number - 1);
                cartMapper.updateNumber(item);
            } else if (number == 1) {
                cartMapper.delete(item.getId());
            } else {
                throw new ShoppingCartBusinessException(MessageConstant.UNKNOWN_ERROR);
            }
        } else {
            throw new ShoppingCartBusinessException(MessageConstant.UNKNOWN_ERROR);
        }

    }

    @Override
    public List<ShoppingCart> list() {
        List<ShoppingCart> list = cartMapper.getByConditions(ShoppingCart
                .builder()
                .userId(BaseContext.getCurrentId())
                .build());
        return list;
    }

    @Override
    public void clean() {
        cartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
