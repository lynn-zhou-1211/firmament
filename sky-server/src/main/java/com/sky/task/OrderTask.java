package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     * 规则：每分钟触发一次，检查是否存在 "待付款" 且 "下单时间 < 当前时间 - 15分钟" 的订单
     */
    @Scheduled(cron = "0 * * * * ? ") // cron表达式：每分钟的第0秒触发
    public void processTimeoutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        // 1. 计算临界时间：当前时间 - 15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // 2. 查询超时订单：select * from orders where status = 1 and order_time < time
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        // 3. 遍历并取消
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中状态的订单
     * 规则：每天凌晨1点触发，把 "派送中" 的订单状态改为 "已完成"
     */
    @Scheduled(cron = "0 0 1 * * ?") // cron表达式：每天凌晨1点触发
    public void processDeliveryOrder() {
        log.info("定时处理处于派送中的订单：{}", LocalDateTime.now());

        // 1. 计算临界时间：当前时间 - 60分钟 (这里的逻辑是：1点跑任务，检查12点之前还在派送的单)
        // 实际上苍穹的业务规则通常是：处理 "上一个工作日" 的所有派送单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        // 2. 查询：select * from orders where status = 4 and order_time < time
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        // 3. 遍历并自动完成
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}