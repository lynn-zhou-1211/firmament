package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setMealMapper;

    /**
     * 保存菜品
     *
     * @param dishDTO 存储对象
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        // 将 dish 存储到 dish 表格中
        // 获取当前 dish 的 id
        // 遍历 dish.flavor 添加 dish_id 的值
        // 将 dish.flavor 插入 dish_flavor 表格中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.insert(dish);
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertList(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 查询对象
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品 id 列表
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断能否删除
        // 1. 是否存在起售中的
        for (Long id : ids) {
            Integer status = dishMapper.queryById(id).getStatus();
            if (status == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 2. 是否被套餐关联
        List<Long> setIds = setMealMapper.getSetMealIdsByDishIds(ids);
        if (setIds != null && !setIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO queryByIdWithFlavor(Long id) {
        Dish dish = dishMapper.queryById(id);
        List<DishFlavor> flavors = dishFlavorMapper.queryByDishId(id);

        // 封装
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 修改菜品表基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品表
        dishMapper.update(dish);

        // 删除原有口味数据
        dishFlavorMapper.deleteByDishIds(Arrays.asList(dishDTO.getId()));
        // 插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertList(flavors);
        }

    }

    @Override
    public void startOrStop(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 如果是停售，还要把包含当前菜品的套餐一起停售
        if(status==StatusConstant.DISABLE){
            // 获取套餐id
            List<Long> ids = Arrays.asList(id);
            List<Long> setIds = setMealMapper.getSetMealIdsByDishIds(ids);
            // 禁用套餐
            if(setIds!=null && !setIds.isEmpty()){
                for (Long sid : setIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(sid)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setMealMapper.update(setmeal);
                }
            }
        }
    }

    @Override
    public List<Dish> queryByCategoryId(Long categoryId) {
        // 只展示起售状态的菜品
        Dish dishes = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dishes);
    }

}
