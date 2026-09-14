package com.sky.service.impl;

import com.sky.dto.OrderStatisticsDTO;
import com.sky.dto.TurnoverDTO;
import com.sky.dto.UserStatisticsDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定区间时间内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }

        // 计算营业额：状态为已完成的订单金额合计
        List<TurnoverDTO> turnoverList = orderMapper.getTurnoverByDate(begin, end, Orders.COMPLETED);

        // 转成 Map<LocalDate, BigDecimal>，方便按日期查找
        Map<LocalDate, BigDecimal> turnoverMap = new HashMap<>();
        for (TurnoverDTO dto : turnoverList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            BigDecimal turnover = dto.getTurnover() == null ? BigDecimal.ZERO : dto.getTurnover();
            turnoverMap.put(date, turnover);
        }

        // 按日期顺序补全营业额，没有订单的日期填 0
        List<BigDecimal> turnoverListResult = new ArrayList<>();
        for (LocalDate date : dateList) {
            BigDecimal turnover = turnoverMap.getOrDefault(date, BigDecimal.ZERO);
            turnoverListResult.add(turnover);
        }

        // 拼接日期字符串和营业额字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String turnoverStr = turnoverListResult.stream()
                .map(BigDecimal::toString)
                .collect(Collectors.joining(","));

        return TurnoverReportVO.builder()
                .dateList(dateStr)
                .turnoverList(turnoverStr)
                .build();
    }

    /**
     * 统计指定区间时间内的用户
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

        // 查询 begin 之前的总用户数（基准）
        LocalDateTime beginTime = begin.atStartOfDay();
        Integer baseTotal = userMapper.getTotalUserCountBefore(beginTime);
        if (baseTotal == null) {
            baseTotal = 0;
        }

        // 查询 begin 到 end 之间每天新增的用户数
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<UserStatisticsDTO> dailyList = userMapper.getDailyNewUserCount(beginTime, endTime);

        // 把新增用户数转成 Map<LocalDate, Integer>，方便按日期查找
        Map<LocalDate, Integer> newUserMap = new HashMap<>();
        for (UserStatisticsDTO dto : dailyList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            newUserMap.put(date, dto.getNewUserNumber());
        }

        // 按日期顺序累加，得到每天的总用户数和新增用户数
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        int cumulative = baseTotal;

        for (LocalDate date : dateList) {
            Integer newUser = newUserMap.getOrDefault(date, 0);
            cumulative += newUser;

            totalUserList.add(cumulative);
            newUserList.add(newUser);
        }

        // 拼接字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String totalUserStr = totalUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String newUserStr = newUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return UserReportVO.builder()
                .dateList(dateStr)
                .totalUserList(totalUserStr)
                .newUserList(newUserStr)
                .build();
    }

    /**
     * 统计指定区间时间内的订单
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

        // 查询每天的订单统计
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<OrderStatisticsDTO> orderStatisticsDTOList = orderMapper.getOrderStatisticsByDate(beginTime, endTime);

        // 转成 Map<LocalDate, OrderStatisticsDTO>，方便按日期查找
        Map<LocalDate, OrderStatisticsDTO> statisticsMap = new HashMap<>();
        for (OrderStatisticsDTO dto : orderStatisticsDTOList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            statisticsMap.put(date, dto);
        }

        // 按日期顺序补全数据，没有订单的日期填 0
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        for (LocalDate date : dateList) {
            OrderStatisticsDTO dto = statisticsMap.get(date);
            int orderCount = 0;
            int validCount = 0;
            if (dto != null) {
                orderCount = dto.getTotalOrderCount() == null ? 0 : dto.getTotalOrderCount();
                validCount = dto.getValidOrderCount() == null ? 0 : dto.getValidOrderCount();
            }
            orderCountList.add(orderCount);
            validOrderCountList.add(validCount);
            totalOrderCount += orderCount;
            validOrderCount += validCount;
        }
        // 计算订单完成率，注意除零
        double orderCompletionRate = 0.0;
        if (totalOrderCount > 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }

        // 拼接字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String orderCountStr = orderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String validOrderCountStr = validOrderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // 构建返回对象
        return OrderReportVO.builder()
                .dateList(dateStr)
                .orderCountList(orderCountStr)
                .validOrderCountList(validOrderCountStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 获取从开始日期到结束日期之间所有日期的列表
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getRangeTime(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }
}
