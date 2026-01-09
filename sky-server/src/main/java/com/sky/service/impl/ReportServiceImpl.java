package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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