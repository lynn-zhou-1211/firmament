package com.sky.controller.user;

import ch.qos.logback.classic.Logger;
import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.utils.RedisUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.SalesTop10ReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "Setmeal module")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("Setmeal query by category")
    public Result<List<Setmeal>> list(Long categoryId) {
        // 设置key，检索，如果查到了就return；如果没查到就查询并存储
        log.info("根据分类查询套餐列表：{}",categoryId);
        String key = "setmeal_" + categoryId;
        List<Setmeal> list = redisUtil.getList(key, Setmeal.class);

        if (list != null && !list.isEmpty()) {
            log.info("查找到 Redis 缓存，key:{}", key);
            return Result.success(list);
        }

        log.info("Redis 缓存不存在，查询数据库，key:{}", key);
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        list = setmealService.list(setmeal);
        redisUtil.set(key, list);

        return Result.success(list);
    }

    /**
     * 根据套餐 id 查询包含的菜品列表
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("Dish list query by setmeal id")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        log.info("根据套餐 id 查询包含的菜品列表：{}",id);
        List<DishItemVO> list = setmealService.queryDishItemById(id);
        return Result.success(list);
    }
}
