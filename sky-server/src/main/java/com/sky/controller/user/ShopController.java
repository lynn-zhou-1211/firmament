package com.sky.controller.user;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Api(tags="Shop Module")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("Shop status query")
    public Result<Integer> getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(RedisKeyConstant.SHOP_STATUS);
        log.info("获取到店铺状态为{}",status==1?"营业中":"'打烊中");
        return Result.success(status);
    }
}
