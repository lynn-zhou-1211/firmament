package com.sky.controller.admin;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import com.sky.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Api(tags="Shop Module")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisUtil redisUtil;

    @PutMapping("/{status}")
    @ApiOperation("Shop set status")
    public Result setStatus(@PathVariable Integer status){
        redisUtil.set(RedisKeyConstant.SHOP_STATUS, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("Shop status query")
    public Result<Integer> getStatus(){
        Integer status = redisUtil.get(RedisKeyConstant.SHOP_STATUS, Integer.class);
        return Result.success(status == null ? 0 : status);
    }
}
