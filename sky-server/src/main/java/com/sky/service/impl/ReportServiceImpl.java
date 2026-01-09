package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils; // 记得检查是否有这个包
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper; // 注入 UserMapper

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表 (和之前一样)
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 2. 准备两个 List 存数据
        List<Integer> newUserList = new ArrayList<>(); // 新增用户
        List<Integer> totalUserList = new ArrayList<>(); // 总用户

        // 3. 循环查询
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 3.1 查询“新增用户” (create_time 在当天范围内)
            // select count(id) from user where create_time >= beginTime and create_time <= endTime
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer newUser = userMapper.countByMap(map);

            // 3.2 查询“总用户” (create_time <= 当天结束时间)
            // select count(id) from user where create_time <= endTime
            map.put("begin", null); // 把开始时间清空，只要小于结束时间就是总量
            Integer totalUser = userMapper.countByMap(map);

            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        // 4. 封装返回
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 2. 准备 List
        List<Integer> orderCountList = new ArrayList<>();      // 每天订单总数
        List<Integer> validOrderCountList = new ArrayList<>(); // 每天有效订单数

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 3.1 查询每天的总订单数
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orderCount = orderMapper.countByMap(map);

            // 3.2 查询每天的有效订单数 (状态 = 已完成)
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        // 4. 计算期间总数 (Stream流写法，也可以用for循环累加)
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 5. 计算完成率 (注意分母不能为0)
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        // 6. 封装返回
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 计算日期列表：从 begin 到 end 的每一天
        // 例如：[2023-10-01, 2023-10-02, 2023-10-03]
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1); // 日期加1天
            dateList.add(begin);
        }

        // 2. 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 查询某一天的营业额
            // 只要是当天的都在范围内： min: 2023-10-01 00:00:00, max: 2023-10-01 23:59:59
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 构造 Map 查询条件
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED); // 必须是已完成的订单

            Double turnover = orderMapper.sumByMap(map);

            // 如果当天没有单子，sum返回null，要转成 0.0
            turnover = turnover == null ? 0.0 : turnover;

            turnoverList.add(turnover);
        }

        // 3. 封装 VO 返回
        // StringUtils.join 可以把 List 转成 "100,200,300" 这种逗号分隔的字符串
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }
}