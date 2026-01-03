package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "Setmeal Module")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("Setmeal create")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增菜品：{}",setmealDTO);
        setmealService.saveSetmealWithDishes(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Setmeal Page Query")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("Setmeal batch delete")
    public Result delete(@RequestParam List<Integer> ids){
        log.info("套餐批量删除：{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("Setmeal update")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("套餐修改：{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Setmeal stutus upsate")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Integer id){
        log.info("修改套餐{}状态：{}",id,status);
        setmealService.startOrStop(id,status);
        return Result.success();
    }

}
