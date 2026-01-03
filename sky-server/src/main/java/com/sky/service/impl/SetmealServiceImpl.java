package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    DishMapper dishMapper;

    @Override
    public void saveSetmealWithDishes(SetmealDTO setmealDTO) {
        // 套餐名称唯一
        // 套餐必须属于某个分类
        // 套餐必须包含菜品
        // 名称、分类、价格、图片为必填项
        // 添加菜品窗口需要根据分类类型来展示菜品
        // 新增的套餐默认为停售状态

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 向套餐表插入数据
        setmealMapper.insert(setmeal);

        // 获取生成的套餐 id
        Long setmealId = setmeal.getId();

        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if (dishes != null && !dishes.isEmpty()) {
            dishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

            // 保存关联关系
            setmealDishMapper.insertBatch(dishes);
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 查询分类名称
        Page<SetmealVO> setmeals = setmealMapper.pageQueryWithCategoryName(setmealPageQueryDTO);
        return new PageResult(setmeals.getTotal(), setmeals.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 起售中的套餐不能删除
        // 删除 setmeal-dishes
        ids.forEach(id -> {
            if (setmealMapper.getById(id).getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            ;
        });
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBatch(ids);
    }

    @Override
    public SetmealVO queryByIdWithCategoryNameAndSetmealDishes(Long id) {
        // 查询单个套餐，带上套餐名
        SetmealVO setmealVO = setmealMapper.getById(id);

        // 查询套餐的 setmeal-dishes， 填充字段
        setmealVO.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        // 更新套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // 批量删除 套餐-菜品
        setmealDishMapper.deleteBatch(Arrays.asList(setmeal.getId()));
        // 批量插入新的套餐-菜品
        Long setmealId = setmeal.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if (dishes != null && !dishes.isEmpty()) {
            dishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

            // 保存关联关系
            setmealDishMapper.insertBatch(dishes);
        }
    }

    @Override
    public void startOrStop(Long id, Integer status) {
        // 起售套餐时，如果套餐内包含停售的菜品，则不能起售
        // 当状态是起售-> 获取setmeal.list -> 逐个获取dish.id -> 判断是否起售
        if (status == StatusConstant.ENABLE) {
            List<SetmealDish> list = setmealDishMapper.getBySetmealId(id);
            for (SetmealDish setmealDish : list) {
                Dish dish=  dishMapper.queryById(setmealDish.getDishId());
                Integer dishStatus =dish.getStatus();
                if (dishStatus == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

}
