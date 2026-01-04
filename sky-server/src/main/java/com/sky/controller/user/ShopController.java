package com.sky.controller.user;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import com.sky.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Api(tags = "Shop Module")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/status")
    @ApiOperation("Shop status query")
    public Result<Integer> getStatus() {
        Integer status = redisUtil.get(RedisKeyConstant.SHOP_STATUS, Integer.class);
        return Result.success(status == null ? 0 : status);
    }
}
