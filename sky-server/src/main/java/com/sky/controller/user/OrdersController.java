package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "Order Module")
@Slf4j
public class OrdersController {
    @Autowired
    OrdersService ordersService;

    @PostMapping("/submit")
    @ApiOperation("Order submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = ordersService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @ApiOperation("Order payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);

        // 1. 这里的 ordersPaymentDTO 里包含了 orderNumber (订单号)
        // 2. 这里的逻辑原本应该是：调微信下单接口 -> 拿预支付ID -> 唤起微信收银台

        // ---------------------------------------------
        // 【修改方案】直接跳过微信，调用 Service 修改订单状态
        // ---------------------------------------------
        ordersService.paySuccess(ordersPaymentDTO.getOrderNumber());

        // 模拟返回一个空数据或者前端需要的格式，让前端以为支付成功了
        return Result.success(new OrderPaymentVO());
    }
}
