package com.sky.controller.admin;

import com.sky.constant.RedisConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.utils.RedisUtil;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "Dish Module")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisUtil redisUtil;

    @PostMapping
    @ApiOperation("Dish create")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        // 清理缓存数据
        redisUtil.delete(RedisConstant.KEY_DISH_PREFIX +dishDTO.getId());

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Dish Page Query")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("查询菜品：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("Dish batch deleteById")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}",ids);
        dishService.deleteBatch(ids);

        // 清除所有 dish 相关缓存
        redisUtil.deleteByPattern(RedisConstant.PATTERN_DISH);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("Dish query by id")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据 id 查询菜品：{}",id);
        DishVO dishVO = dishService.queryByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("Dish update")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("更新菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        redisUtil.deleteByPattern(RedisConstant.PATTERN_DISH);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Dish status update")
    public Result<String> startOrStop(@PathVariable Integer status,@RequestParam Long id){
        log.info("菜品 {} 起售/停售：{}",id,status);
        dishService.startOrStop(id,status);
        redisUtil.deleteByPattern(RedisConstant.PATTERN_DISH);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("Dish query by category id")
    public Result<List<Dish>> getByCategoryId(@RequestParam Long categoryId){
        log.info("根据分类 id 查询菜品：{}",categoryId);
        List<Dish> dishes = dishService.queryByCategoryId(categoryId);
        return Result.success(dishes);
    }
}
