package com.sky.controller.admin;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
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
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("Shop set status")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态为{}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(RedisKeyConstant.SHOP_STATUS,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("Shop status query")
    public Result<Integer> getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(RedisKeyConstant.SHOP_STATUS);
        log.info("获取到店铺状态为{}",status==1?"营业中":"'打烊中");
        return Result.success(status);
    }
}
