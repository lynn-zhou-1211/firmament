package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "Order Module")
@Slf4j
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("Order submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @ApiOperation("Order payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO.getOrderNumber());
        OrderPaymentVO orderPaymentVO = orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("模拟支付成功，生成虚拟交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询订单详情
     *
     * @param id 订单id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("Order details")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 历史订单查询
     */
    @GetMapping("/historyOrders")
    @ApiOperation("Order history")
    public Result<PageResult> page(int pageNum, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(pageNum, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("Order cancel")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     * 逻辑：把原订单的商品重新加入购物车
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("Order again")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
}
