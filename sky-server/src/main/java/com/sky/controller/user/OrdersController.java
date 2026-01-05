package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
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
        log.info("订单支付：{}", ordersPaymentDTO.getOrderNumber());
        OrderPaymentVO orderPaymentVO = ordersService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("模拟支付成功，生成虚拟交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询订单详情
     * (补上这个方法，为了解决你之前遇到的 404 报错)
     *
     * @param id 订单id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        log.info("查询订单详情：{}", id);
        // 如果你的 Service 层还没有这个 details 方法，你需要去 OrderService 接口和实现类里加上
        // 暂时如果没写，可以先注释掉下面这行，返回 null 防止报错
        OrderVO orderVO = ordersService.details(id);
        return Result.success(orderVO);
    }
}
